package uk.co.eluinhost.ultrahardcore.features;

import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.Feature;

public class UHCFeature extends Feature {

    public static final String BASE_PERMISSION = "UHC.";
    public static final String BASE_CONFIG = "features.";

    private ConfigManager m_manager = ConfigManager.getInstance();

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

    /**
     * @param key the key to search for
     * @return the translated message
     */
    public String translate(String key){
        return m_manager.getMessage(key);
    }
}
