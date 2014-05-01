/*
 * UHCModule.java
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

import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.RealFeatureManager;
import com.publicuhc.ultrahardcore.metrics.UHCMetrics;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.ultrahardcore.borders.BorderTypeManager;
import com.publicuhc.ultrahardcore.borders.RealBorderTypeManager;
import com.publicuhc.ultrahardcore.scatter.FallProtector;
import com.publicuhc.ultrahardcore.scatter.Protector;
import com.publicuhc.ultrahardcore.scatter.RealScatterManager;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import org.mcstats.Metrics;

@SuppressWarnings("OverlyCoupledMethod")
public class UHCModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FeatureManager.class).to(RealFeatureManager.class);
        bind(BorderTypeManager.class).to(RealBorderTypeManager.class);
        bind(Protector.class).to(FallProtector.class);
        bind(ScatterManager.class).to(RealScatterManager.class);
        bind(Metrics.class).to(UHCMetrics.class);
    }
}
