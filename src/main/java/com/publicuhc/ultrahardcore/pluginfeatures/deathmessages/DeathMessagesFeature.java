/*
 * DeathMessagesFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.deathmessages;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;


/**
 * DeathMessagesFeature
 * Handles changes to death messages on death
 *
 * @author ghowden
 */
@Singleton
public class DeathMessagesFeature extends UHCFeature {

    public static final String BASE_MESSAGES = BASE_PERMISSION + "death_messages.";
    public static final String DEATH_MESSAGE_SUPPRESSED = BASE_MESSAGES + "remove";
    public static final String DEATH_MESSAGE_AFFIXES = BASE_MESSAGES + "affixes";

    /**
     * Change the format of death messages
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private DeathMessagesFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * Whenver a player dies
     * @param pde the death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (isEnabled()) {
            //if death message suppression is on
            if (getConfigManager().getConfig("main").getBoolean(getBaseConfig() + "remove")) {
                //and the players death messages are suppressed
                if (pde.getEntity().hasPermission(DEATH_MESSAGE_SUPPRESSED)) {
                    //set to nothing
                    pde.setDeathMessage("");
                }
                return;
            }
            //if there is an affix for the player
            if (pde.getEntity().hasPermission(DEATH_MESSAGE_AFFIXES)) {
                //grab format from config file
                String format = ChatColor.translateAlternateColorCodes('&', getConfigManager().getConfig("main").getString(getBaseConfig() + "message"));

                //replace vars
                format = format.replaceAll("%message", pde.getDeathMessage());
                format = format.replaceAll("%player", pde.getEntity().getName());
                Location loc = pde.getEntity().getLocation();
                format = format.replaceAll("%coords", locationString(loc));

                //set the new message
                pde.setDeathMessage(format);
            }
        }
    }

    /**
     * Returns string in the format x:X y:Y z:Z
     * @param loc Location
     * @return String
     */
    private static String locationString(Location loc) {
        return "x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ();
    }

    @Override
    public String getFeatureID() {
        return "DeathMessages";
    }

    @Override
    public String getDescription() {
        return "Adds a prefix/suffix to all player deaths";
    }
}
