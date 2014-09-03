package com.publicuhc.ultrahardcore;

import com.publicuhc.pluginframework.shaded.inject.multibindings.Multibinder;
import com.publicuhc.ultrahardcore.api.Command;
import com.publicuhc.ultrahardcore.api.UHCAddonConfiguration;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import com.publicuhc.ultrahardcore.commands.*;
import com.publicuhc.ultrahardcore.features.*;
import org.bukkit.Bukkit;

@SuppressWarnings({"OverlyCoupledMethod", "OverlyCoupledClass"})
public class UHCCoreAddonConfiguration implements UHCAddonConfiguration
{
    @Override
    public void configureFeatures(Multibinder<UHCFeature> features)
    {
        features.addBinding().to(EnderpearlsFeature.class);
        features.addBinding().to(GhastDropsFeature.class);
        features.addBinding().to(NetherFeature.class);
        features.addBinding().to(PlayerFreezeFeature.class);
        features.addBinding().to(PlayerListFeature.class);
        features.addBinding().to(PortalsFeature.class);
        features.addBinding().to(RecipeFeature.class);
        features.addBinding().to(RegenFeature.class);
        features.addBinding().to(UberApplesFeature.class);
        features.addBinding().to(WitchSpawnsFeature.class);

        if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            features.addBinding().to(HardcoreHeartsFeature.class);
        }
    }

    @Override
    public void configureCommands(Multibinder<Command> commands)
    {
        commands.addBinding().to(ClearInventoryCommand.class);
        commands.addBinding().to(FeatureCommand.class);
        commands.addBinding().to(FeedCommand.class);
        commands.addBinding().to(FreezeCommand.class);
        commands.addBinding().to(HealCommand.class);
        commands.addBinding().to(TPCommand.class);
        commands.addBinding().to(WhitelistCommands.class);
        commands.addBinding().to(DebugCommands.class);
    }
}
