package uk.co.eluinhost.ultrahardcore.features;

import uk.co.eluinhost.features.Feature;

public class UHCFeature extends Feature {

    public static final String BASE_PERMISSION = "UHC.";
    public static final String BASE_CONFIG = "features.";

    /**
     * Construct a new feature
     *
     * @param featureID   the feature ID to use
     * @param description the description for the feature
     */
    protected UHCFeature(String featureID, String description) {
        super(featureID, description);
    }

    /**
     * @return base config node for this feature with trailing .
     */
    public String getBaseConfig(){
        return BASE_CONFIG+getFeatureID()+".";
    }
}
