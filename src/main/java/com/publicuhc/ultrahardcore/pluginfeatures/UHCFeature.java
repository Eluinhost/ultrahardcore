/*
 * UHCFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures;

import com.publicuhc.ultrahardcore.features.Feature;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.plugin.Plugin;

public abstract class UHCFeature extends Feature {

    protected static final String BASE_PERMISSION = "UHC.";
    protected static final String BASE_CONFIG = "features.";

    /**
     * Construct a new feature
     *
     * @param plugin the plugin to use
     * @param configManager the config manager to use
     * @param translate the translator
     */
    @Inject
    protected UHCFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * @return base config node for this feature with trailing .
     */
    public String getBaseConfig(){
        return BASE_CONFIG+getFeatureID()+".";
    }
}
