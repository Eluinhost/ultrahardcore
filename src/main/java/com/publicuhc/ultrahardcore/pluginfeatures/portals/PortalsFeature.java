/*
 * PortalsFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.portals;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.ChatColor;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class PortalsFeature extends UHCFeature {

    /**
     * Changes the radius portals will connect, normal when disabled
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private PortalsFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * When a portal event happends
     * @param entityPortalEvent the portal event
     */
    @EventHandler
    public void onPortalEvent(PlayerPortalEvent entityPortalEvent) {
        //if we're enabled
        if (isEnabled()) {
            FileConfiguration config = getConfigManager().getConfig("main");

            //create a travel agent for the portal
            TravelAgent ta = entityPortalEvent.getPortalTravelAgent();
            //if they're in the nether
            if (entityPortalEvent.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
                //set data from the nether
                ta.setCanCreatePortal(config.getBoolean(getBaseConfig()+"from_nether.allowed"));
                ta.setCreationRadius(config.getInt(getBaseConfig()+"from_nether.creation_radius"));
                ta.setSearchRadius(config.getInt(getBaseConfig()+"from_nether.search_radius"));
            } else {
                //set the data to the nether
                ta.setCanCreatePortal(config.getBoolean(getBaseConfig()+"to_nether.allowed"));
                ta.setCreationRadius(config.getInt(getBaseConfig()+"to_nether.creation_radius"));
                ta.setSearchRadius(config.getInt(getBaseConfig()+"to_nether.search_radius"));
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "PortalRanges";
    }

    @Override
    public String getDescription() {
        return "Change the radius portals can spawn in";
    }

    @Override
    public List<String> getStatus() {
        List<String> status = new ArrayList<String>();
        status.add(ChatColor.GRAY + "--- Allow from nether: "+convertBooleanToOnOff(getConfigManager().getConfig("main").getBoolean(getBaseConfig() + "from_nether.allowed")));
        status.add(ChatColor.GRAY + "--- From nether creation radius: "+getConfigManager().getConfig("main").getInt(getBaseConfig()+"from_nether.creation_radius"));
        status.add(ChatColor.GRAY + "--- From nether search radius: "+getConfigManager().getConfig("main").getInt(getBaseConfig()+"from_nether.search_radius"));
        status.add(ChatColor.GRAY + "--- Allow to nether: "+convertBooleanToOnOff(getConfigManager().getConfig("main").getBoolean(getBaseConfig() + "to_nether.allowed")));
        status.add(ChatColor.GRAY + "--- To nether creation radius: "+getConfigManager().getConfig("main").getInt(getBaseConfig()+"to_nether.creation_radius"));
        status.add(ChatColor.GRAY + "--- To nether search radius: "+getConfigManager().getConfig("main").getInt(getBaseConfig()+"to_nether.search_radius"));
        return status;
    }
}
