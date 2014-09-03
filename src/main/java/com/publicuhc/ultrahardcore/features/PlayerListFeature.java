/*
 * PlayerListFeature.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.features;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * PlayerListFeature
 *
 * Enabled: Handles showing health in the player list
 * Disabled: Nothing
 */
@Singleton
public class PlayerListFeature extends UHCFeature {

    public static final String PLAYER_LIST_HEALTH = "UHC.playerListHealth";

    //the internal bukkit id for the task
    private int m_taskID = -1;

    //the list of players and their health that we are handling
    private static final Map<Player, Double> HANDLED_PLAYERS = new WeakHashMap<Player, Double>();

    private static final Scoreboard MAIN_SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();

    private static final int MAX_LENGTH_COLOURS = 14;
    private static final int MAX_LENGTH_NO_COLOURS = 16;

    private static final String OBJECTIVE_SCOREBOARD_NAME = "UHCHealth";
    private static final String OBJECTIVE_UNDER_NAME_NAME = "UHCHealthName";
    private static final String OBJECTIVE_TYPE = "dummy";

    private static final double LOW_HEALTH_BOUNDARY = 33;
    private static final double MID_HEALTH_BOUNDARY = 66;

    private static final Range<Double> DEAD_HEALTH_PERCENT = Ranges.closed(0.0D, 0.0D);
    private static final Range<Double> LOW_HEALTH_PERCENT = Ranges.openClosed(0.0D, LOW_HEALTH_BOUNDARY);
    private static final Range<Double> MIDDLE_HEALTH_PERCENT = Ranges.openClosed(LOW_HEALTH_BOUNDARY, MID_HEALTH_BOUNDARY);

    private Objective playerListObjective;
    private Objective underNameObjective;

    private final FileConfiguration config;
    private final Plugin plugin;

    /**
     * Handles the player list health better than base mc, normal behaviour when disabled
     *
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    private PlayerListFeature(Plugin plugin, Configurator configManager) {
        Optional<FileConfiguration> mainConfig = configManager.getConfig("main");
        if(!mainConfig.isPresent()) {
            throw new IllegalStateException("Config file 'main' was not found, cannot find configuration values");
        }
        config = mainConfig.get();this.plugin = plugin;
    }

    /**
     * update the players name in the list with the following health number
     * @param player the player to update
     * @param health the health value to update to
     */
    public void updatePlayerListHealth(Player player, double health) {
        String playerName = config.getBoolean("PlayerList.displayNames") ? player.getDisplayName() : player.getName();

        //strip the colour codes from the player name
        String cutName = ChatColor.stripColor(playerName);

        //whether to use colours or not
        boolean useColours = config.getBoolean("PlayerList.colours");

        //maximum length allowed for the player name to not crash people
        int maxLength = useColours ? MAX_LENGTH_COLOURS : MAX_LENGTH_NO_COLOURS;

        //cut the name down to the right length
        cutName = cutName.substring(0, Math.min(cutName.length(), maxLength));

        if (useColours) {
            ChatColor prefix = ChatColor.GREEN;

            double healthPercent = health / player.getMaxHealth() * 100;

            if (player.hasPermission(PLAYER_LIST_HEALTH)) {
                if (MIDDLE_HEALTH_PERCENT.contains(healthPercent)) {
                    prefix = ChatColor.YELLOW;
                } else if (LOW_HEALTH_PERCENT.contains(healthPercent)) {
                    prefix = ChatColor.RED;
                } else if (DEAD_HEALTH_PERCENT.contains(healthPercent)) {
                    prefix = ChatColor.GRAY;
                }
            } else {
                prefix = ChatColor.BLUE;
                health = 0.0D;
            }
            cutName = prefix + cutName;
        }

        //set the player list name
        player.setPlayerListName(cutName);

        //if we're rounding health
        if (config.getBoolean("PlayerList.roundHealth")) {
            health = Math.ceil(health);
        }

        double healthScaling = config.getDouble("PlayerList.scaling");

        //set the score for both the player and their display name
        //this allows the score to show under the head of players with a changed name
        playerListObjective.getScore(cutName).setScore((int) (health * healthScaling));
        underNameObjective.getScore(ChatColor.stripColor(playerName)).setScore((int) (health * healthScaling));
    }

    /**
     * Update all the players supplied
     * @param onlinePlayers list of online players
     */
    public void updatePlayers(Player[] onlinePlayers) {
        for (Player player : onlinePlayers) {
            //get the existing health
            Double health = HANDLED_PLAYERS.get(player);
            //if there isn't one
            if (health == null) {
                //set it to 0.0 and add the player
                health = 0.0D;
                HANDLED_PLAYERS.put(player, health);
            }
            //if the health has changed
            if (!health.equals(player.getHealth())) {
                //update with the new values
                updatePlayerListHealth(player, player.getHealth());
            }
        }
    }

    @Override
    protected void enableCallback() {
        //set up the timer that runs
        m_taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                new PlayerListUpdater(),
                1L,
                config.getLong("PlayerList.delay")
        );
        //intialize the scoreboard
        initializeScoreboard();
    }

    @Override
    protected void disableCallback() {
        //disable the task if its running
        if (m_taskID >= 0) {
            Bukkit.getScheduler().cancelTask(m_taskID);
            m_taskID = -1;
        }
        //if the scoreboard is there
        if (MAIN_SCOREBOARD != null) {
            //clear the slots we use
            MAIN_SCOREBOARD.clearSlot(DisplaySlot.PLAYER_LIST);
            MAIN_SCOREBOARD.clearSlot(DisplaySlot.BELOW_NAME);
            //reset the player list name for all online players to their name
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.setPlayerListName(p.getName());
            }
        }
    }

    /**
     * Initialize our scoreboard
     */
    private void initializeScoreboard() {
        //try to make new objectives, throws exception when it already exists
        try {
            MAIN_SCOREBOARD.registerNewObjective(OBJECTIVE_SCOREBOARD_NAME, OBJECTIVE_TYPE);
        } catch (IllegalArgumentException ignored) {}
        try {
            MAIN_SCOREBOARD.registerNewObjective(OBJECTIVE_UNDER_NAME_NAME, OBJECTIVE_TYPE);
        } catch (IllegalArgumentException ignored) {}

        //set the objectives we created
        playerListObjective = MAIN_SCOREBOARD.getObjective(OBJECTIVE_SCOREBOARD_NAME);
        underNameObjective = MAIN_SCOREBOARD.getObjective(OBJECTIVE_UNDER_NAME_NAME);

        //set the display name of the under name objective
        underNameObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("PlayerList.belowNameUnit")).replaceAll("&h", "\u2665"));

        //set the slot for player list health
        playerListObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        //if under name is enabled
        if (config.getBoolean("PlayerList.belowName")) {
            //set it's slot
            underNameObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        } else {
            //get the objective that is below the name
            Objective o = MAIN_SCOREBOARD.getObjective(DisplaySlot.BELOW_NAME);
            //if its our objective clear the slot
            if (o != null && o.getName().equals(OBJECTIVE_UNDER_NAME_NAME)) {
                MAIN_SCOREBOARD.clearSlot(DisplaySlot.BELOW_NAME);
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "PlayerList";
    }

    @Override
    public String getDescription() {
        return "Player's health shown in player list and under their name";
    }

    @Override
    public List<String> getStatus() {
        List<String> status = new ArrayList<String>();
        status.add(ChatColor.GRAY + "--- Colours: " + convertBooleanToOnOff(config.getBoolean("PlayerList.colours")));
        status.add(ChatColor.GRAY + "--- Update delay: " + config.getInt("PlayerList.delay"));
        status.add(ChatColor.GRAY + "--- Below Name: " + convertBooleanToOnOff(config.getBoolean("PlayerList.belowName")));
        status.add(ChatColor.GRAY + "--- Scaling: " + config.getInt("PlayerList.scaling"));
        status.add(ChatColor.GRAY + "--- Rounding: " + convertBooleanToOnOff(config.getBoolean("PlayerList.roundHealth")));
        String unit = ChatColor.translateAlternateColorCodes('&', config.getString("PlayerList.belowNameUnit")).replaceAll("&h", "\u2665");
        status.add(ChatColor.GRAY + "--- Unit: " + unit);
        status.add(ChatColor.GRAY + "--- Use display names: " + convertBooleanToOnOff(config.getBoolean("PlayerList.displayNames")));
        return status;
    }

    private class PlayerListUpdater implements Runnable {
        @Override
        public void run() {
            updatePlayers(Bukkit.getOnlinePlayers());
        }
    }
}
