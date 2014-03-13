package uk.co.eluinhost.ultrahardcore.features;

import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.Feature;

public class UHCFeature extends Feature {

    public static final String BASE_PERMISSION = "UHC.";
    public static final String BASE_CONFIG = "features.";

    /**
     * Construct a new feature
     *
     * @param featureID   the feature ID to use
     * @param description the description for the feature
     * @param configManager the config manager to use
     */
    protected UHCFeature(Plugin plugin, String featureID, String description, ConfigManager configManager) {
        super(plugin, featureID, description, configManager);
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
        return getConfigManager().getMessage(key);
    }
}
