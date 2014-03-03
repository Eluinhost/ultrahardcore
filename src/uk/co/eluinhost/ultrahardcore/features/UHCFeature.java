package uk.co.eluinhost.ultrahardcore.features;

import uk.co.eluinhost.features.Feature;

public class UHCFeature extends Feature {

    public static final String BASE_PERMISSION = "UHC.";

    /**
     * Construct a new feature
     *
     * @param featureID   the feature ID to use
     * @param description the description for the feature
     */
    protected UHCFeature(String featureID, String description) {
        super(featureID, description);
    }
}
