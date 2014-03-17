package uk.co.eluinhost.features;

import uk.co.eluinhost.features.exceptions.FeatureIDConflictException;
import uk.co.eluinhost.features.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.features.exceptions.InvalidFeatureIDException;

import java.util.List;

public interface FeatureManager {

    /**
     * Add a feature to the manager
     * counts as enabled if the feature name is in the config list
     *
     * @param feature Feature the feature to be added
     * @throws uk.co.eluinhost.features.exceptions.FeatureIDConflictException when feature with the same ID already exists
     * @throws uk.co.eluinhost.features.exceptions.InvalidFeatureIDException  when the feature has an invalid ID name
     */
    void addFeature(IFeature feature) throws FeatureIDConflictException, InvalidFeatureIDException;

    /**
     * Check if a feature is enabled by it's ID
     *
     * @param featureID String the ID to check for
     * @return boolean true if enabled, false otherwise
     * @throws uk.co.eluinhost.features.exceptions.FeatureIDNotFoundException when feature not found
     */
    boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException;

    /**
     * Get the Feature based on it's ID
     *
     * @param featureID String the ID to check for
     * @return Feature the returned feature, or null if not found
     */
    IFeature getFeatureByID(String featureID);

    /**
     * Returns an unmodifiable list of all of the uhcFeatures loaded
     *
     * @return List
     */
    List<IFeature> getFeatures();

    /**
     * Get a list of all the used feature names
     *
     * @return List String
     */
    List<String> getFeatureNames();
}
