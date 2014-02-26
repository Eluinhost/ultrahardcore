package uk.co.eluinhost.ultrahardcore.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.exceptions.features.FeatureIDConflictException;
import uk.co.eluinhost.ultrahardcore.exceptions.features.FeatureIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.features.InvalidFeatureIDException;
import uk.co.eluinhost.ultrahardcore.features.*;
import uk.co.eluinhost.ultrahardcore.events.features.UHCFeatureInitEvent;

/**
 * Feature Manager Class
 */
public class FeatureManager {

    @SuppressWarnings("UtilityClass")
    private static final class LazyFeatureManagerHolder {
        private static final FeatureManager INSTANCE = new FeatureManager();
    }

    /**
     * @return feature manager instance
     */
    public static FeatureManager getInstance(){
        return LazyFeatureManagerHolder.INSTANCE;
    }

    /**
     * Feature manager
     */
    private FeatureManager(){}

    /**
     * Stores a list of all the uhcFeatures loaded on the server
     */
    private final List<IUHCFeature> m_uhcFeatureList = new ArrayList<IUHCFeature>();

    /**
     * Only allow uhcFeatures with this pattern as an ID
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\w]++$");

    /**
     * Add a UHC feature to the manager
     *
     * @param feature UHCFeature the feature to be added
     * @param enabled Whether the feature should be enabled or not after init
     * @throws FeatureIDConflictException when feature with the same ID already exists
     * @throws InvalidFeatureIDException  when the feature has an invalid ID name
     */
    public void addFeature(UHCFeature feature, boolean enabled) throws FeatureIDConflictException, InvalidFeatureIDException {
        String featureID = feature.getFeatureID();

        //check for right pattern
        Matcher mat = NAME_PATTERN.matcher(featureID);
        if (!mat.matches()) {
            throw new InvalidFeatureIDException();
        }

        //check for existing feature of the same name
        for (IUHCFeature uhcFeature : m_uhcFeatureList) {
            if (uhcFeature.equals(feature)) {
                throw new FeatureIDConflictException();
            }
        }

        //Make an init event for the feature creation
        UHCFeatureInitEvent initEvent = new UHCFeatureInitEvent(feature);

        //call the event
        Bukkit.getPluginManager().callEvent(initEvent);

        //if it was cancelled return
        if (initEvent.isCancelled()) {
            Bukkit.getLogger().log(Level.SEVERE,"Init event cancelled for feature "+featureID);
            return;
        }

        //add the feature
        m_uhcFeatureList.add(feature);
        Bukkit.getLogger().log(Level.INFO,"Loaded feature module "+featureID);

        if(enabled){
            feature.enableFeature();
        }else{
            feature.disableFeature();
        }

        //Register the feature for plugin events
        Bukkit.getPluginManager().registerEvents(feature, UltraHardcore.getInstance());
    }

    /**
     * Check if a feature is enabled by it's ID
     *
     * @param featureID String the ID to check for
     * @return boolean true if enabled, false otherwise
     * @throws FeatureIDNotFoundException when feature not found
     */
    public boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException {
        for (IUHCFeature feature : m_uhcFeatureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature.isEnabled();
            }
        }
        throw new FeatureIDNotFoundException();
    }

    /**
     * Get the UHCFeature based on it's ID
     *
     * @param featureID String the ID to check for
     * @return UHCFeature the returned feature
     * @throws FeatureIDNotFoundException when feature ID not found
     */
    public IUHCFeature getFeatureByID(String featureID) throws FeatureIDNotFoundException {
        for (IUHCFeature feature : m_uhcFeatureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature;
            }
        }
        throw new FeatureIDNotFoundException();
    }

    /**
     * Returns an unmodifiable list of all of the uhcFeatures loaded
     *
     * @return List
     */
    public List<IUHCFeature> getFeatures() {
        return Collections.unmodifiableList(m_uhcFeatureList);
    }

    /**
     * Get a list of all the used feature names
     *
     * @return List String
     */
    public List<String> getFeatureNames() {
        List<String> features = new ArrayList<String>();
        for (IUHCFeature uhc : m_uhcFeatureList) {
            features.add(uhc.getFeatureID());
        }
        return features;
    }
}
