package com.publicuhc.ultrahardcore.api;

import com.publicuhc.pluginframework.PluginModule;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.multibindings.Multibinder;
import com.publicuhc.pluginframework.translate.TranslateModule;
import org.bukkit.plugin.Plugin;

/**
 * All of the services that are individual for each addon
 */
public class ServicesModule extends AbstractModule {

    private final Plugin instance;
    private final UHCAddonConfiguration configuration;

    public ServicesModule(Plugin instance, UHCAddonConfiguration configuration)
    {
        this.instance = instance;
        this.configuration = configuration;
    }

    @Override
    protected void configure()
    {
        //bind the things
        configuration.configureCommands(Multibinder.newSetBinder(binder(), Command.class));
        configuration.configureFeatures(Multibinder.newSetBinder(binder(), UHCFeature.class));

        //translate per plugin, locales bound in shared services
        install(new TranslateModule());

        //plugin module separate for the modules that use it and for logger injection
        install(new PluginModule(instance));

        //configuration per addon plugin
        install(new ConfigurationModule(instance.getClass().getClassLoader()));
    }
}
