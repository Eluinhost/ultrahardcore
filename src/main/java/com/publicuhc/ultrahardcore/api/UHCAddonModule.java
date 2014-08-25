package com.publicuhc.ultrahardcore.api;

import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.multibindings.Multibinder;

public abstract class UHCAddonModule extends AbstractModule {

    protected abstract void registerFeatures(Multibinder<UHCFeature> binder);

    protected abstract void registerCommands(Multibinder<Command> binder);

    @Override
    protected void configure() {
        registerCommands(Multibinder.newSetBinder(binder(), Command.class));
        registerFeatures(Multibinder.newSetBinder(binder(), UHCFeature.class));
    }
}
