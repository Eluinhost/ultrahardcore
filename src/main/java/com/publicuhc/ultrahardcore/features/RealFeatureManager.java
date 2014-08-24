/*
 * RealFeatureManager.java
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

import com.publicuhc.ultrahardcore.features.events.FeatureInitEvent;
import com.publicuhc.ultrahardcore.features.exceptions.FeatureIDConflictException;
import com.publicuhc.ultrahardcore.features.exceptions.FeatureIDNotFoundException;
import com.publicuhc.ultrahardcore.features.exceptions.InvalidFeatureIDException;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Feature Manager Class
 */
@Singleton
public class RealFeatureManager implements FeatureManager {

    private final Configurator m_configManager;
    private final Plugin m_plugin;

    /**
     * Create a new feature manager
     * @param configManager the config manager
     * @param plugin the plugin
     */
    @Inject
    public RealFeatureManager(Configurator configManager, Plugin plugin){
        m_configManager = configManager;
        m_plugin = plugin;
    }

    /**
     * Stores a list of all the features loaded on the server
     */
    private final List<Feature> m_featureList = new ArrayList<Feature>();

    /**
     * Only allow uhcFeatures with this pattern as an ID
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\S]++$");

    @Override
    public void addFeature(Feature feature) throws FeatureIDConflictException, InvalidFeatureIDException {
        String featureID = feature.getFeatureID();

        //check for right pattern
        Matcher mat = NAME_PATTERN.matcher(featureID);
        if (!mat.matches()) {
            throw new InvalidFeatureIDException();
        }

        //check for existing feature of the same name
        for (Feature uhcFeature : m_featureList) {
            if (uhcFeature.equals(feature)) {
                throw new FeatureIDConflictException();
            }
        }

        //Make an init event for the feature creation
        FeatureInitEvent initEvent = new FeatureInitEvent(feature);

        //call the event
        Bukkit.getPluginManager().callEvent(initEvent);

        //if it was cancelled return
        if (initEvent.isCancelled()) {
            Bukkit.getLogger().log(Level.SEVERE,"Init event cancelled for feature "+featureID);
            return;
        }

        //add the feature
        m_featureList.add(feature);
        Bukkit.getLogger().log(Level.INFO,"Loaded feature module "+featureID);

        List<String> config = m_configManager.getConfig("main").getStringList("enabledFeatures");
        if(config.contains(featureID)){
            feature.enableFeature();
        }else{
            feature.disableFeature();
        }

        //Register the feature for plugin events
        Bukkit.getPluginManager().registerEvents(feature, m_plugin);
    }

    @Override
    public boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException {
        for (Feature feature : m_featureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature.isEnabled();
            }
        }
        throw new FeatureIDNotFoundException();
    }

    @Override
    public Feature getFeatureByID(String featureID) {
        for (Feature feature : m_featureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature;
            }
        }
        return null;
    }

    @Override
    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(m_featureList);
    }

    @Override
    public List<String> getFeatureNames() {
        List<String> features = new ArrayList<String>();
        for (Feature feature : m_featureList) {
            features.add(feature.getFeatureID());
        }
        return features;
    }
}
