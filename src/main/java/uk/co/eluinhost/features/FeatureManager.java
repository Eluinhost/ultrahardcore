package uk.co.eluinhost.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.events.FeatureInitEvent;
import uk.co.eluinhost.features.exceptions.FeatureIDConflictException;
import uk.co.eluinhost.features.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.features.exceptions.InvalidFeatureIDException;

/**
 * Feature Manager Class
 */
public class FeatureManager {

    private final ConfigManager m_configManager;
    private final Plugin m_plugin;

    public FeatureManager(ConfigManager configManager, Plugin plugin){
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

    /**
     * Add a UHC feature to the manager
     * counts as enabled if the feature name is in the config list
     *
     * @param feature Feature the feature to be added
     * @throws FeatureIDConflictException when feature with the same ID already exists
     * @throws InvalidFeatureIDException  when the feature has an invalid ID name
     */
    public void addFeature(Feature feature) throws FeatureIDConflictException, InvalidFeatureIDException {
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

    /**
     * Check if a feature is enabled by it's ID
     *
     * @param featureID String the ID to check for
     * @return boolean true if enabled, false otherwise
     * @throws FeatureIDNotFoundException when feature not found
     */
    public boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException {
        for (IFeature feature : m_featureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature.isEnabled();
            }
        }
        throw new FeatureIDNotFoundException();
    }

    /**
     * Get the Feature based on it's ID
     *
     * @param featureID String the ID to check for
     * @return Feature the returned feature, or null if not found
     */
    public IFeature getFeatureByID(String featureID) {
        for (IFeature feature : m_featureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable list of all of the uhcFeatures loaded
     *
     * @return List
     */
    public List<IFeature> getFeatures() {
        return Collections.unmodifiableList(m_featureList);
    }

    /**
     * Get a list of all the used feature names
     *
     * @return List String
     */
    public List<String> getFeatureNames() {
        List<String> features = new ArrayList<String>();
        for (IFeature feature : m_featureList) {
            features.add(feature.getFeatureID());
        }
        return features;
    }
}
