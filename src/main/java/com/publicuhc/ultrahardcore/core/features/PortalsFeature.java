/*
 * PortalsFeature.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.ultrahardcore.core.features;

import com.google.common.base.Optional;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import org.bukkit.ChatColor;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * PortalsFeature
 * <p/>
 * Enabled: Changes the search/creation radius for portals
 * Disabled: Nothing
 */
@Singleton
public class PortalsFeature extends UHCFeature
{

    private final FileConfiguration config;

    /**
     * Changes the radius portals will connect, normal when disabled
     *
     * @param plugin        the plugin
     * @param configManager the config manager
     * @param translate     the translator
     */
    @Inject
    private PortalsFeature(Plugin plugin, Configurator configManager, Translate translate)
    {
        Optional<FileConfiguration> mainConfig = configManager.getConfig("main");
        if(!mainConfig.isPresent()) {
            throw new IllegalStateException("Config file 'main' was not found, cannot find configuration values");
        }
        config = mainConfig.get();
    }

    /**
     * When a portal event happends
     *
     * @param entityPortalEvent the portal event
     */
    @EventHandler
    public void onPortalEvent(PlayerPortalEvent entityPortalEvent)
    {
        //if we're enabled
        if(isEnabled()) {

            //create a travel agent for the portal
            TravelAgent ta = entityPortalEvent.getPortalTravelAgent();
            //if they're in the nether
            if(entityPortalEvent.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
                //set data from the nether
                ta.setCanCreatePortal(config.getBoolean("PortalRanges.from_nether.allowed"));
                ta.setCreationRadius(config.getInt("PortalRanges.from_nether.creation_radius"));
                ta.setSearchRadius(config.getInt("PortalRanges.from_nether.search_radius"));
            } else {
                //set the data to the nether
                ta.setCanCreatePortal(config.getBoolean("PortalRanges.to_nether.allowed"));
                ta.setCreationRadius(config.getInt("PortalRanges.to_nether.creation_radius"));
                ta.setSearchRadius(config.getInt("PortalRanges.to_nether.search_radius"));
            }
        }
    }

    @Override
    public String getFeatureID()
    {
        return "PortalRanges";
    }

    @Override
    public String getDescription()
    {
        return "Change the radius portals can spawn in";
    }

    @Override
    public List<String> getStatus()
    {
        List<String> status = new ArrayList<String>();
        status.add(ChatColor.GRAY + "--- Allow from nether: " + convertBooleanToOnOff(config.getBoolean("PortalRanges.from_nether.allowed")));
        status.add(ChatColor.GRAY + "--- From nether creation radius: " + config.getInt("PortalRanges.from_nether.creation_radius"));
        status.add(ChatColor.GRAY + "--- From nether search radius: " + config.getInt("PortalRanges.from_nether.search_radius"));
        status.add(ChatColor.GRAY + "--- Allow to nether: " + convertBooleanToOnOff(config.getBoolean("PortalRanges.to_nether.allowed")));
        status.add(ChatColor.GRAY + "--- To nether creation radius: " + config.getInt("PortalRanges.to_nether.creation_radius"));
        status.add(ChatColor.GRAY + "--- To nether search radius: " + config.getInt("PortalRanges.to_nether.search_radius"));
        return status;
    }
}
