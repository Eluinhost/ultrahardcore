package com.publicuhc.ultrahardcore.features;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import org.bukkit.plugin.Plugin;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.features.Feature;

public abstract class UHCFeature extends Feature {

    protected static final String BASE_PERMISSION = "UHC.";
    protected static final String BASE_CONFIG = "features.";

    /**
     * Construct a new feature
     *
     * @param plugin the plugin to use
     * @param configManager the config manager to use
     */
    @Inject
    protected UHCFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
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
