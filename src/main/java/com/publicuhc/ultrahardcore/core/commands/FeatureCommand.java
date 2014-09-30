/*
 * FeatureCommand.java
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

package com.publicuhc.ultrahardcore.core.commands;

import com.publicuhc.pluginframework.routing.annotation.CommandMethod;
import com.publicuhc.pluginframework.routing.annotation.CommandOptions;
import com.publicuhc.pluginframework.routing.annotation.OptionsMethod;
import com.publicuhc.pluginframework.routing.annotation.PermissionRestriction;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.addons.FeatureManager;
import com.publicuhc.ultrahardcore.api.Command;
import com.publicuhc.ultrahardcore.api.Feature;
import com.publicuhc.ultrahardcore.api.FeatureValueConverter;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public class FeatureCommand implements Command
{

    public static final String FEATURE_LIST_PERMISSION = "UHC.feature.list";
    public static final String FEATURE_TOGGLE_PERMISSION = "UHC.feature.toggle";

    private final FeatureManager featureManager;
    private final FeatureValueConverter featureValueConverter;
    private final Translate translate;

    @Inject
    private FeatureCommand(Translate translate, FeatureManager featureManager, FeatureValueConverter featureValueConverter)
    {
        this.translate = translate;
        this.featureManager = featureManager;
        this.featureValueConverter = featureValueConverter;
    }

    private void turnFeatureOff(Feature feature, CommandSender sender)
    {
        if(feature.disableFeature()) {
            translate.sendMessage("features.disabled", sender);
        } else {
            translate.sendMessage("features.disabled_cancelled", sender);
        }
    }

    private void turnFeatureOn(Feature feature, CommandSender sender)
    {
        if(feature.enableFeature()) {
            translate.sendMessage("features.enabled", sender);
        } else {
            translate.sendMessage("features.enabled_cancelled", sender);
        }
    }

    private void toggleFeature(Feature feature, CommandSender sender)
    {
        if(feature.isEnabled()) {
            turnFeatureOff(feature, sender);
        } else {
            turnFeatureOn(feature, sender);
        }
    }

    @CommandMethod("feature list")
    @PermissionRestriction(FEATURE_LIST_PERMISSION)
    public void featureListCommand(OptionSet set, CommandSender sender)
    {
        Collection<Feature> features = featureManager.getFeatures();
        translate.sendMessage("features.loaded.header", sender, features.size());
        if(features.isEmpty()) {
            translate.sendMessage("features.loaded.none", sender);
        }
        for(Feature feature : features) {
            translate.sendMessage(feature.isEnabled() ? "features.loaded.on" : "features.loaded.off", sender, feature.getFeatureID(), feature.getDescription());

            List<String> status = feature.getStatus();

            for(String message : status) {
                sender.sendMessage(message);
            }
        }
    }

    @CommandMethod("feature toggle")
    @PermissionRestriction(FEATURE_TOGGLE_PERMISSION)
    @CommandOptions("[arguments]")
    public void featureToggleCommand(OptionSet set, CommandSender sender, List<Feature> features)
    {
        if(features.isEmpty()) {
            translate.sendMessage("supply one feature name", sender);
            return;
        }
        for(Feature feature : features) {
            toggleFeature(feature, sender);
        }
    }

    @OptionsMethod
    public void featureToggleCommand(OptionDeclarer parser)
    {
        parser.nonOptions().withValuesConvertedBy(featureValueConverter);
    }

    @CommandMethod("feature on")
    @PermissionRestriction(FEATURE_TOGGLE_PERMISSION)
    @CommandOptions("[arguments]")
    public void featureOnCommand(OptionSet set, CommandSender sender, List<Feature> features)
    {
        if(features.isEmpty()) {
            translate.sendMessage("supply one feature name", sender);
            return;
        }
        for(Feature feature : features) {
            turnFeatureOn(feature, sender);
        }
    }

    @OptionsMethod
    public void featureOnCommand(OptionDeclarer parser)
    {
        parser.nonOptions().withValuesConvertedBy(featureValueConverter);
    }

    @CommandMethod("feature off")
    @PermissionRestriction(FEATURE_TOGGLE_PERMISSION)
    @CommandOptions("[arguments]")
    public void featureOffCommand(OptionSet set, CommandSender sender, List<Feature> features)
    {
        if(features.isEmpty()) {
            translate.sendMessage("supply one feature name", sender);
            return;
        }
        for(Feature feature : features) {
            turnFeatureOff(feature, sender);
        }
    }

    @OptionsMethod
    public void featureOffCommand(OptionDeclarer parser)
    {
        parser.nonOptions().withValuesConvertedBy(featureValueConverter);
    }
}
