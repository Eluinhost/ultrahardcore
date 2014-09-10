/*
 * DefaultFeatureManager.java
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

package com.publicuhc.ultrahardcore.addons;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.api.Feature;
import com.publicuhc.ultrahardcore.api.events.FeatureInitEvent;
import com.publicuhc.ultrahardcore.api.exceptions.FeatureIDConflictException;
import com.publicuhc.ultrahardcore.api.exceptions.FeatureIDNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

@Singleton
public class DefaultFeatureManager implements FeatureManager
{
    /**
     * Only allow features with this pattern as an ID (no whitespace)
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\S]++$");
    private final FileConfiguration config;
    private final Plugin plugin;
    private final PluginLogger logger;
    /**
     * Stores a list of all the features loaded on the server
     */
    private final List<Feature> featureList = new ArrayList<Feature>();

    /**
     * Create a new feature manager
     *
     * @param configManager the config manager
     * @param plugin        the plugin
     */
    @Inject
    public DefaultFeatureManager(Configurator configManager, Plugin plugin, PluginLogger logger)
    {
        this.plugin = plugin;
        this.logger = logger;
        Optional<FileConfiguration> mainConfig = configManager.getConfig("main");
        if(!mainConfig.isPresent()) {
            throw new IllegalStateException("Config file 'main' was not found, cannot find configuration values");
        }
        config = mainConfig.get();
    }

    @Override
    public void addFeature(Feature feature) throws FeatureIDConflictException
    {
        String featureID = feature.getFeatureID();
        logger.log(Level.INFO, "Loading feature: " + featureID);
        Preconditions.checkArgument(NAME_PATTERN.matcher(featureID).matches(), "Invalid feature ID: %s, cannot contain whitespace", featureID);

        //check for existing feature of the same name
        for(Feature uhcFeature : featureList) {
            if(uhcFeature.equals(feature)) {
                throw new FeatureIDConflictException();
            }
        }

        //Make an init event for the feature creation
        FeatureInitEvent initEvent = new FeatureInitEvent(feature);

        //call the event
        Bukkit.getPluginManager().callEvent(initEvent);

        //if it was cancelled return
        if(initEvent.isCancelled()) {
            logger.log(Level.SEVERE, "Init event cancelled for feature: " + featureID);
            return;
        }

        //add the feature
        featureList.add(feature);
        logger.log(Level.INFO, "Loaded feature: " + featureID);

        List<String> configs = config.getStringList("enabledFeatures");
        if(configs.contains(featureID)) {
            feature.enableFeature();
        } else {
            feature.disableFeature();
        }

        //Register the feature for plugin events
        Bukkit.getPluginManager().registerEvents(feature, plugin);
    }

    @Override
    public boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException
    {
        for(Feature feature : featureList) {
            if(feature.getFeatureID().equals(featureID)) {
                return feature.isEnabled();
            }
        }
        throw new FeatureIDNotFoundException();
    }

    @Override
    public Optional<Feature> getFeatureByID(String featureID)
    {
        for(Feature feature : featureList) {
            if(feature.getFeatureID().equals(featureID)) {
                return Optional.of(feature);
            }
        }
        return Optional.absent();
    }

    @Override
    public List<Feature> getFeatures()
    {
        return Collections.unmodifiableList(featureList);
    }

    @Override
    public List<String> getFeatureNames()
    {
        List<String> features = new ArrayList<String>();
        for(Feature feature : featureList) {
            features.add(feature.getFeatureID());
        }
        return features;
    }
}
