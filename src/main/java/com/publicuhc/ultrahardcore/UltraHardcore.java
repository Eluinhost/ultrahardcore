/*
 * UltraHardcore.java
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
package com.publicuhc.ultrahardcore;

import com.publicuhc.pluginframework.FrameworkJavaPlugin;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.pluginfeatures.deathbans.DeathBan;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.mcstats.Metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * UltraHardcore
 * <p/>
 * Main plugin class, init
 *
 * @author ghowden
 */
@Singleton
public class UltraHardcore extends FrameworkJavaPlugin {

    private DefaultClasses m_defaults;

    //When the plugin gets started
    @Override
    public void onFrameworkEnable() {
        //register deathbans for serilization
        ConfigurationSerialization.registerClass(DeathBan.class);
        //register the bungeecord plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        m_defaults.loadDefaultFeatures();
        m_defaults.loadDefaultScatterTypes();
        m_defaults.loadDefaultCommands();
        if(Bukkit.getPluginManager().getPlugin("WorldEdit") != null){
            m_defaults.loadBorders();
        }
        getLogger().log(Level.INFO, "All default classes loaded");
    }

    /**
     * Load all the defaults for the entire plugin
     * @param defaultClasses init class
     */
    @Inject
    public void loadDefaultClasses(DefaultClasses defaultClasses) {
        m_defaults = defaultClasses;
    }

    /**
     * Load the metrics class and start it
     * @param metrics the metrics to use
     */
    @Inject
    public void loadMetrics(Metrics metrics) {
        metrics.start();
    }

    @Override
    public List<AbstractModule> initialModules() {
        List<AbstractModule> customModules = new ArrayList<AbstractModule>();
        customModules.add(new UHCModule());
        return customModules;
    }
}