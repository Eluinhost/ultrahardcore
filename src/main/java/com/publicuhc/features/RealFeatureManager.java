package com.publicuhc.features;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.features.events.FeatureInitEvent;
import com.publicuhc.features.exceptions.FeatureIDConflictException;
import com.publicuhc.features.exceptions.FeatureIDNotFoundException;
import com.publicuhc.features.exceptions.InvalidFeatureIDException;

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

    private final ConfigManager m_configManager;
    private final Plugin m_plugin;

    /**
     * Create a new feature manager
     * @param configManager the config manager
     * @param plugin the plugin
     */
    @Inject
    public RealFeatureManager(ConfigManager configManager, Plugin plugin){
        m_configManager = configManager;
        m_plugin = plugin;
    }

    /**
     * Stores a list of all the features loaded on the server
     */
    private final List<IFeature> m_featureList = new ArrayList<IFeature>();

    /**
     * Only allow uhcFeatures with this pattern as an ID
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\w]++$");

    @Override
    public void addFeature(IFeature feature) throws FeatureIDConflictException, InvalidFeatureIDException {
        String featureID = feature.getFeatureID();

        //check for right pattern
        Matcher mat = NAME_PATTERN.matcher(featureID);
        if (!mat.matches()) {
            throw new InvalidFeatureIDException();
        }

        //check for existing feature of the same name
        for (IFeature uhcFeature : m_featureList) {
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

        List<String> config = m_configManager.getConfig().getStringList("enabledFeatures");
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
        for (IFeature feature : m_featureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature.isEnabled();
            }
        }
        throw new FeatureIDNotFoundException();
    }

    @Override
    public IFeature getFeatureByID(String featureID) {
        for (IFeature feature : m_featureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature;
            }
        }
        return null;
    }

    @Override
    public List<IFeature> getFeatures() {
        return Collections.unmodifiableList(m_featureList);
    }

    @Override
    public List<String> getFeatureNames() {
        List<String> features = new ArrayList<String>();
        for (IFeature feature : m_featureList) {
            features.add(feature.getFeatureID());
        }
        return features;
    }
}
