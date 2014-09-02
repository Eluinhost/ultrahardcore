package com.publicuhc.ultrahardcore;

import com.publicuhc.pluginframework.shaded.inject.multibindings.Multibinder;
import com.publicuhc.ultrahardcore.api.Command;
import com.publicuhc.ultrahardcore.api.UHCAddonModule;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import com.publicuhc.ultrahardcore.commands.*;
import com.publicuhc.ultrahardcore.features.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class UHCCoreAddonModule extends UHCAddonModule {

    protected UHCCoreAddonModule(Plugin instance)
    {
        super(instance);
    }

    @Override
    protected void registerFeatures(Multibinder<UHCFeature> binder) {
        binder.addBinding().to(EnderpearlsFeature.class);
        binder.addBinding().to(GhastDropsFeature.class);
        binder.addBinding().to(NetherFeature.class);
        binder.addBinding().to(PlayerFreezeFeature.class);
        binder.addBinding().to(PlayerListFeature.class);
        binder.addBinding().to(PortalsFeature.class);
        binder.addBinding().to(RecipeFeature.class);
        binder.addBinding().to(RegenFeature.class);
        binder.addBinding().to(UberApplesFeature.class);
        binder.addBinding().to(WitchSpawnsFeature.class);

        if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
            binder.addBinding().to(HardcoreHeartsFeature.class);
    }

    @Override
    protected void registerCommands(Multibinder<Command> binder) {
        binder.addBinding().to(ClearInventoryCommand.class);
        binder.addBinding().to(FeatureCommand.class);
        binder.addBinding().to(FeedCommand.class);
        binder.addBinding().to(FreezeCommand.class);
        binder.addBinding().to(HealCommand.class);
        binder.addBinding().to(TPCommand.class);
        binder.addBinding().to(WhitelistCommands.class);
    }
}
