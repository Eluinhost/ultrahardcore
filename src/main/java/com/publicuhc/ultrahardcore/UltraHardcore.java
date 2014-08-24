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
import com.publicuhc.pluginframework.routing.Router;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.shaded.metrics.Metrics;
import com.publicuhc.ultrahardcore.features.Feature;
import com.publicuhc.ultrahardcore.features.FeatureManager;

import java.util.ArrayList;
import java.util.List;

/**
 * UltraHardcore
 * <p/>
 * Main plugin class, init
 *
 * @author ghowden
 */
@Singleton
public class UltraHardcore extends FrameworkJavaPlugin {

    private FeatureManager featureManager;

    //When the plugin gets started
    @Override
    protected void onFrameworkEnable()
    {
        //enable metrics
        Metrics metrics = getMetrics();
        Metrics.Graph graph = metrics.createGraph("Features Loaded");
        for(final Feature feature : featureManager.getFeatures()){
            graph.addPlotter(new Metrics.Plotter(feature.getFeatureID()) {
                @Override
                public int getValue() {
                    return feature.isEnabled() ? 1 : 0;
                }
            });
        }
        metrics.addGraph(graph);
        metrics.start();
    }

    @Inject
    private void setFeatureManager(FeatureManager featureManager)
    {
        this.featureManager = featureManager;
    }

    /**
     * @return the feature manager for handling features
     */
    public FeatureManager getFeatureManager()
    {
        return featureManager;
    }

    /**
     * @return the plugin router, used for registering commands e.t.c.
     */
    public Router getCommandRouter()
    {
        return getRouter();
    }

    @Override
    protected List<AbstractModule> initialModules()
    {
        List<AbstractModule> customModules = new ArrayList<AbstractModule>();
        customModules.add(new UHCModule());
        return customModules;
    }
}