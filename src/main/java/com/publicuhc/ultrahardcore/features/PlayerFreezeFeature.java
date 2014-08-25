/*
 * PlayerFreezeFeature.java
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

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.commands.FreezeCommand;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * PlayerFreezeFeature
 *
 * Enabled: Allows freezing of players with commands
 * Disabled: Nothing
 */
@Singleton
public class PlayerFreezeFeature extends UHCFeature {

    private boolean globalMode = false;

    private final FreezeRunnable freezer;
    private final FileConfiguration config;
    private final Plugin plugin;

    /**
     * handles frozen players
     *
     * @param plugin        the plugin
     * @param configManager the config manager
     * @param translate     the translator
     */
    @Inject
    private PlayerFreezeFeature(Plugin plugin, Configurator configManager, Translate translate, PluginLogger logger) {
        this.plugin = plugin;
        config = configManager.getConfig("main");

        List<String> potionEffectsList = config.getStringList("PlayerFreeze.potion.effects");

        int duration = config.getInt("PlayerFreezepotion.duration");
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        for (String potionEffectString : potionEffectsList) {
            String[] parts = potionEffectString.split(":");
            if (parts.length != 2) {
                logger.log(Level.SEVERE, "Potion effect " + potionEffectString + " does not contain a ':', skipping it.");
                continue;
            }

            int amplifier = -1;
            try {
                amplifier = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {}

            if (amplifier < 0) {
                logger.log(Level.SEVERE, "Potion effect " + potionEffectString + " has an invalid potion effect level '" + parts[1] + "', skipping it");
                continue;
            }

            PotionEffectType type = PotionEffectType.getByName(parts[0]);

            if (null == type) {
                logger.log(Level.SEVERE, "Potion effect " + potionEffectString + " has an invalid potion effect type '" + parts[0] + "', skipping it");
                continue;
            }

            effects.add(new PotionEffect(type, duration, amplifier, true));
        }

        freezer = new FreezeRunnable(effects);
        Bukkit.getPluginManager().registerEvents(freezer, plugin);
    }

    /**
     * @param player the entity to freeze
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public void addPlayer(Player player) {
        if (player.hasPermission(FreezeCommand.ANTIFREEZE_PERMISSION) || freezer.isPlayerFrozen(player)) {
            return;
        }
        freezer.addPlayer(player);
    }

    public void removePlayer(Player player) {
        freezer.removePlayer(player);
    }

    /**
     * @param playerID the player id
     */
    public void removePlayer(UUID playerID) {
        freezer.removePlayer(playerID);
    }

    public boolean isPlayerFrozen(Player player) {
        return freezer.isPlayerFrozen(player);
    }

    public boolean isPlayerFrozen(UUID uuid) {
        return freezer.isPlayerFrozen(uuid);
    }

    /**
     * Remove all from the frozen list and sets global off
     */
    public void unfreezeAll() {
        globalMode = false;
        freezer.clear();
    }

    /**
     * Adds all to the list and sets global on
     */
    public void freezeAll() {
        globalMode = true;
        freezer.addPlayers(Bukkit.getOnlinePlayers());
    }

    public boolean isGlobalMode() {
        return globalMode;
    }

    /**
     * Whenever a player joins
     *
     * @param pje the player join event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent pje) {
        if (globalMode || freezer.isPlayerFrozen(pje.getPlayer())) {
            addPlayer(pje.getPlayer());
        } else {
            removePlayer(pje.getPlayer());
        }
    }

    /**
     * Called when the feature is being disabled
     */
    @Override
    protected void disableCallback() {
        freezer.cancel();
    }

    @Override
    protected void enableCallback() {
        freezer.runTaskTimer(plugin, 0, config.getInt("PlayerFreeze.period"));
    }

    @Override
    public String getFeatureID() {
        return "PlayerFreeze";
    }

    @Override
    public String getDescription() {
        return "Allows for freezing players in place";
    }
}
