package uk.co.eluinhost.ultrahardcore.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.exceptions.FeatureIDConflictException;
import uk.co.eluinhost.ultrahardcore.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.InvalidFeatureIDException;
import uk.co.eluinhost.ultrahardcore.features.events.UHCFeatureInitEvent;

/**
 * Feature Manager Class
 * TODO make this not a utility class
 */
public final class FeatureManager {

    /**
     * Stores a list of all the UHC_FEATURES loaded on the server
     */
    private static final List<UHCFeature> UHC_FEATURES = new ArrayList<UHCFeature>();

    /**
     * Only allow UHC_FEATURES with this pattern as an ID
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\w]++$");

    /**
     * Don't allow constuct
     */
    private FeatureManager() {}

    /**
     * Add a UHC feature to the manager
     *
     * @param feature UHCFeature the feature to be added
     * @param enabled Whether the feature should be enabled or not after init
     * @throws FeatureIDConflictException when feature with the same ID already exists
     * @throws InvalidFeatureIDException  when the feature has an invalid ID name
     */
    public static void addFeature(UHCFeature feature, boolean enabled) throws FeatureIDConflictException, InvalidFeatureIDException {

        //check for alphanumerics
        Matcher mat = NAME_PATTERN.matcher(feature.getFeatureID());
        if (!mat.matches()) {
            throw new InvalidFeatureIDException();
        }

        //check for existing feature of the same name
        for (UHCFeature uhcFeature : UHC_FEATURES) {
            if (uhcFeature.getFeatureID().equals(feature.getFeatureID())) {
                throw new FeatureIDConflictException();
            }
        }

        //Make an init event for the feature creation
        UHCFeatureInitEvent initEvent = new UHCFeatureInitEvent(feature);

        //call the event
        Bukkit.getServer().getPluginManager().callEvent(initEvent);

        //if it was cancelled return
        if (!initEvent.isAllowed()) {
            return;
        }

        //add the feature

        //TODO change this >.>
        UHC_FEATURES.add(feature);
        if (feature.isEnabled()) {
            feature.enableFeature();
        } else {
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
    public static boolean isEnabled(String featureID) throws FeatureIDNotFoundException {
        for (UHCFeature feature : UHC_FEATURES) {
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
    public static UHCFeature getFeature(String featureID) throws FeatureIDNotFoundException {
        for (UHCFeature feature : UHC_FEATURES) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature;
            }
        }
        throw new FeatureIDNotFoundException();
    }

    /**
     * Returns an unmodifiable list of all of the UHC_FEATURES loaded
     *
     * @return List
     */
    public static List<UHCFeature> getFeatures() {
        return Collections.unmodifiableList(UHC_FEATURES);
    }

    /**
     * Get a list of all the used feature names
     *
     * @return List String
     */
    public static List<String> getFeatureNames() {
        List<String> features = new ArrayList<String>();
        for (UHCFeature uhc : UHC_FEATURES) {
            features.add(uhc.getFeatureID());
        }
        return features;
    }
}
