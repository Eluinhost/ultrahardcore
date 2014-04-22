package com.publicuhc.ultrahardcore.features;

import com.publicuhc.features.Feature;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.plugin.Plugin;

public abstract class UHCFeature extends Feature {

    protected static final String BASE_PERMISSION = "UHC.";
    protected static final String BASE_CONFIG = "features.";

    /**
     * Construct a new feature
     *
     * @param plugin the plugin to use
     * @param configManager the config manager to use
     * @param translate the translator
     */
    @Inject
    protected UHCFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * @return base config node for this feature with trailing .
     */
    public String getBaseConfig(){
        return BASE_CONFIG+getFeatureID()+".";
    }
}
