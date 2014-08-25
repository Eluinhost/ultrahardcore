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

package com.publicuhc.ultrahardcore.api;

import com.google.common.base.Preconditions;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.api.events.FeatureInitEvent;
import com.publicuhc.ultrahardcore.api.exceptions.FeatureIDConflictException;
import com.publicuhc.ultrahardcore.api.exceptions.FeatureIDNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Feature Manager Class
 */
@Singleton
public class DefaultFeatureManager implements FeatureManager {

    private final Configurator configManager;
    private final Plugin plugin;

    /**
     * Create a new feature manager
     * @param configManager the config manager
     * @param plugin the plugin
     */
    @Inject
    public DefaultFeatureManager(Configurator configManager, Plugin plugin){
        this.configManager = configManager;
        this.plugin = plugin;
    }

    /**
     * Stores a list of all the features loaded on the server
     */
    private final List<Feature> featureList = new ArrayList<Feature>();

    /**
     * Only allow features with this pattern as an ID (no whitespace)
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\S]++$");

    @Override
    public void addFeature(Feature feature) throws FeatureIDConflictException {
        String featureID = feature.getFeatureID();
        Preconditions.checkArgument(NAME_PATTERN.matcher(featureID).matches(), "Invalid feature ID: %s, cannot contain whitespace", featureID);

        //check for existing feature of the same name
        for (Feature uhcFeature : featureList) {
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
        featureList.add(feature);
        Bukkit.getLogger().log(Level.INFO,"Loaded feature module "+featureID);

        List<String> config = configManager.getConfig("main").getStringList("enabledFeatures");
        if(config.contains(featureID)){
            feature.enableFeature();
        }else{
            feature.disableFeature();
        }

        //Register the feature for plugin events
        Bukkit.getPluginManager().registerEvents(feature, plugin);
    }

    @Override
    public boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException {
        for (Feature feature : featureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature.isEnabled();
            }
        }
        throw new FeatureIDNotFoundException();
    }

    @Override
    public Feature getFeatureByID(String featureID) {
        for (Feature feature : featureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature;
            }
        }
        return null;
    }

    @Override
    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(featureList);
    }

    @Override
    public List<String> getFeatureNames() {
        List<String> features = new ArrayList<String>();
        for (Feature feature : featureList) {
            features.add(feature.getFeatureID());
        }
        return features;
    }
}
