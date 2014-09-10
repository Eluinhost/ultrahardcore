/*
 * UHCCoreAddonConfiguration.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.core;

import com.publicuhc.pluginframework.shaded.inject.multibindings.Multibinder;
import com.publicuhc.ultrahardcore.api.Command;
import com.publicuhc.ultrahardcore.api.UHCAddonConfiguration;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import com.publicuhc.ultrahardcore.core.commands.*;
import com.publicuhc.ultrahardcore.core.features.*;
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
        commands.addBinding().to(ClearXPCommand.class);
    }
}
