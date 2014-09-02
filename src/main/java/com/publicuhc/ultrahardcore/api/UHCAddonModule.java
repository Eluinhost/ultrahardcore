package com.publicuhc.ultrahardcore.api;

import com.publicuhc.pluginframework.PluginModule;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.multibindings.Multibinder;
import com.publicuhc.pluginframework.translate.TranslateModule;
import org.bukkit.plugin.Plugin;

public abstract class UHCAddonModule extends AbstractModule {

    private final Plugin instance;

    protected UHCAddonModule(Plugin instance)
    {
        this.instance = instance;
    }

    protected abstract void registerFeatures(Multibinder<UHCFeature> binder);

    protected abstract void registerCommands(Multibinder<Command> binder);

    @Override
    protected void configure() {
        registerCommands(Multibinder.newSetBinder(binder(), Command.class));
        registerFeatures(Multibinder.newSetBinder(binder(), UHCFeature.class));

        //register to allow plugin/translate/configuration per addon, don't add router as they should use the UHC one
        install(new PluginModule(instance));
        install(new TranslateModule());
        install(new ConfigurationModule(instance.getClass().getClassLoader()));
    }
}
