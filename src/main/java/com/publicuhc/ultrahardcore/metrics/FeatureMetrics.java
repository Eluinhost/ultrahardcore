/*
 * FeatureMetrics.java
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

package com.publicuhc.ultrahardcore.metrics;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

@Singleton
public class FeatureMetrics {

    private final Metrics m_metrics;

    /**
     * Handle the metrics for loaded features
     * @param metrics the metrics class
     * @param featureManager the feature manager
     */
    @Inject
    private FeatureMetrics(Metrics metrics, FeatureManager featureManager){
        m_metrics = metrics;
        Graph graph = metrics.createGraph("Features Loaded");

        for(final IFeature feature : featureManager.getFeatures()){
            graph.addPlotter(new Metrics.Plotter(feature.getFeatureID()) {
                @Override
                public int getValue() {
                    return feature.isEnabled() ? 1 : 0;
                }
            });
        }
        m_metrics.addGraph(graph);
    }


    /**
     * Start running metrics
     */
    public void start() {
        m_metrics.start();
    }
}
