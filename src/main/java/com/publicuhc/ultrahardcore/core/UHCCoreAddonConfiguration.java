/*
 * UHCCoreAddonConfiguration.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
