/*
 * Feature.java
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

import com.publicuhc.ultrahardcore.features.events.FeatureDisableEvent;
import com.publicuhc.ultrahardcore.features.events.FeatureEnableEvent;
import com.publicuhc.ultrahardcore.features.events.FeatureEvent;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class Feature implements IFeature {

    /**
     * Is the feautre enabeld right now?
     */
    private boolean m_enabled;

    private final Configurator m_configManager;
    private final Plugin m_plugin;
    private final Translate m_translate;

    @Override
    public final boolean enableFeature(){
        if(isEnabled()){
            return false;
        }
        FeatureEvent event = new FeatureEnableEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            m_enabled = true;
            enableCallback();
        }
        return true;
    }

    @Override
    public final boolean disableFeature(){
        if(!isEnabled()){
            return false;
        }
        FeatureDisableEvent event = new FeatureDisableEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            m_enabled = false;
            disableCallback();
        }
        return true;
    }

    /**
     * Called when the feature is being enabled
     */
    protected void enableCallback(){}

    /**
     * Called when the feature is being disabled
     */
    protected void disableCallback(){}

    @Override
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Construct a new feature
     * @param plugin the plugin
     * @param configManager the config manager to use
     * @param translate the translator
     */
    @Inject
    protected Feature(Plugin plugin, Configurator configManager, Translate translate) {
        m_configManager = configManager;
        m_plugin = plugin;
        m_translate = translate;
    }

    /**
     * Are the features the same feature? Returns true if they have the same ID
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IFeature && ((IFeature) obj).getFeatureID().equals(getFeatureID());
    }

    /**
     * @return the config manager for the feature
     */
    protected Configurator getConfigManager(){
        return m_configManager;
    }

    /**
     * @return the plugin for the feature
     */
    protected Plugin getPlugin(){
        return m_plugin;
    }

    /**
     * Return the hashcode of this feature
     * @return int
     */
    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getFeatureID()).toHashCode();
    }

    /**
     * @return the translator
     */
    protected Translate getTranslate() {
        return m_translate;
    }
}
