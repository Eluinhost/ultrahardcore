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

package com.publicuhc.ultrahardcore.pluginfeatures.playerlist;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

/**
 * PlayerListHandler
 * Handles the playerlist health numbers for HANDLED_PLAYERS
 *
 * @author ghowden
 */
@Singleton
public class PlayerListFeature extends UHCFeature {

    public static final String PLAYER_LIST_HEALTH = BASE_PERMISSION + "playerListHealth";

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

    private static final double LOW_HEALTH_BOUNDARY = 6.0D;
    private static final double MID_HEALTH_BOUNDARY = 12.0D;

    private static final Range<Double> DEAD_HEALTH = Ranges.closed(0.0D, 0.0D);
    private static final Range<Double> LOW_HEALTH = Ranges.openClosed(0.0D, LOW_HEALTH_BOUNDARY);
    private static final Range<Double> MIDDLE_HEALTH = Ranges.openClosed(LOW_HEALTH_BOUNDARY, MID_HEALTH_BOUNDARY);
    private static final Range<Double> HIGH_HEALTH = Ranges.greaterThan(MID_HEALTH_BOUNDARY);

    private Objective m_objectivePlayerList;
    private Objective m_objectiveUnderName;

    /**
     * Handles the player list health better than base mc, normal behaviour when disabled
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private PlayerListFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * update the players name in the list with the following health number
     * @param player the player to update
     * @param health the health value to update to
     */
    public void updatePlayerListHealth(Player player, double health) {
        FileConfiguration config = getConfigManager().getConfig("main");

        String playerName = config.getBoolean(getBaseConfig()+"displayNames") ? player.getDisplayName() : player.getName();
        //get the players display name and strip the colour codes from it
        String newName = ChatColor.stripColor(playerName);

        boolean useColours = config.getBoolean(getBaseConfig()+"colours");

        //cut the name down to the right length
        newName = newName.substring(0,Math.min(newName.length(),useColours ? MAX_LENGTH_COLOURS : MAX_LENGTH_NO_COLOURS));

        double showHealth = health;

        if (useColours) {
            ChatColor prefix = ChatColor.GREEN;
            if(HIGH_HEALTH.contains(health)){
                prefix = ChatColor.GREEN;
            }else if(MIDDLE_HEALTH.contains(health)){
                prefix = ChatColor.YELLOW;
            }else if(LOW_HEALTH.contains(health)){
                prefix = ChatColor.RED;
            }else if(DEAD_HEALTH.contains(health)){
                prefix = ChatColor.GRAY;
            }
            if (!player.hasPermission(PLAYER_LIST_HEALTH)) {
                prefix = ChatColor.BLUE;
                showHealth = 0.0D;
            }
            newName = prefix+newName;
        }

        //set the player list name
        player.setPlayerListName(newName);

        //if we're rounding health
        if (config.getBoolean(getBaseConfig()+"roundHealth")) {
            showHealth = Math.ceil(showHealth);
        }

        double healthScaling = config.getDouble(getBaseConfig() + "scaling");
        //set the score for both the player and their display name
        //this allows the score to show under the head of players with a changed name
        m_objectivePlayerList.getScore(newName).setScore((int) (showHealth * healthScaling));
        m_objectiveUnderName.getScore(ChatColor.stripColor(playerName)).setScore((int) (showHealth * healthScaling));
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
                getPlugin(),
                new PlayerListUpdater(),
                1L,
                getConfigManager().getConfig("main").getLong(getBaseConfig() + "delay")
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
        m_objectivePlayerList = MAIN_SCOREBOARD.getObjective(OBJECTIVE_SCOREBOARD_NAME);
        m_objectiveUnderName = MAIN_SCOREBOARD.getObjective(OBJECTIVE_UNDER_NAME_NAME);

        FileConfiguration config = getConfigManager().getConfig("main");

        //set the display name of the under name objective
        m_objectiveUnderName.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(getBaseConfig() + "belowNameUnit")).replaceAll("&h", "\u2665"));

        //set the slot for player list health
        m_objectivePlayerList.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        //if under name is enabled
        if (config.getBoolean(getBaseConfig() + "belowName")) {
            //set it's slot
            m_objectiveUnderName.setDisplaySlot(DisplaySlot.BELOW_NAME);
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
        status.add(ChatColor.GRAY + "--- Colours: " + convertBooleanToOnOff(getConfigManager().getConfig("main").getBoolean(getBaseConfig() + "colours")));
        status.add(ChatColor.GRAY + "--- Update delay: " + getConfigManager().getConfig("main").getInt(getBaseConfig() + "delay"));
        status.add(ChatColor.GRAY + "--- Below Name: " + convertBooleanToOnOff(getConfigManager().getConfig("main").getBoolean(getBaseConfig() + "belowName")));
        status.add(ChatColor.GRAY + "--- Scaling: " + getConfigManager().getConfig("main").getInt(getBaseConfig() + "scaling"));
        status.add(ChatColor.GRAY + "--- Rounding: " + convertBooleanToOnOff(getConfigManager().getConfig("main").getBoolean(getBaseConfig() + "roundHealth")));
        String unit = ChatColor.translateAlternateColorCodes('&', getConfigManager().getConfig("main").getString(getBaseConfig() + "belowNameUnit")).replaceAll("&h", "\u2665");
        status.add(ChatColor.GRAY + "--- Unit: " + unit);
        status.add(ChatColor.GRAY + "--- Use display names: " + convertBooleanToOnOff(getConfigManager().getConfig("main").getBoolean(getBaseConfig() + "displayNames")));
        return status;
    }

    private class PlayerListUpdater implements Runnable {
        @Override
        public void run() {
            updatePlayers(Bukkit.getOnlinePlayers());
        }
    }
}
