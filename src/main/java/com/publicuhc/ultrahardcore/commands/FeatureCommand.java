/*
 * FeatureCommand.java
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

package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.routing.annotation.CommandMethod;
import com.publicuhc.pluginframework.routing.annotation.CommandOptions;
import com.publicuhc.pluginframework.routing.annotation.OptionsMethod;
import com.publicuhc.pluginframework.routing.annotation.PermissionRestriction;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Feature;
import com.publicuhc.ultrahardcore.api.FeatureManager;
import com.publicuhc.ultrahardcore.api.FeatureValueConverter;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureCommand extends TranslatingCommand {

    public static final String FEATURE_LIST_PERMISSION = "UHC.feature.list";
    public static final String FEATURE_TOGGLE_PERMISSION = "UHC.feature.toggle";

    private final FeatureManager featureManager;
    private final FeatureValueConverter featureValueConverter;

    /**
     * @param translate the translator
     * @param featureManager the feature manager
     */
    @Inject
    private FeatureCommand(Translate translate, FeatureManager featureManager){
        super(translate);
        this.featureManager = featureManager;
        featureValueConverter = new FeatureValueConverter(featureManager);
    }

    private void turnFeatureOn(Feature feature, CommandSender sender)
    {
        if(feature.disableFeature()) {
            sender.sendMessage(translate("features.disabled", sender));
        } else {
            sender.sendMessage(translate("features.disabled_cancelled", sender));
        }
    }

    private void turnFeatureOff(Feature feature, CommandSender sender)
    {
        if(feature.enableFeature()) {
            sender.sendMessage(translate("features.enabled", sender));
        } else {
            sender.sendMessage(translate("features.enabled_cancelled", sender));
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
        List<Feature> features = featureManager.getFeatures();
        sender.sendMessage(translate("features.loaded.header", sender, "amount", String.valueOf(features.size())));
        if (features.isEmpty()) {
            sender.sendMessage(translate("features.loaded.none", sender));
        }
        for (Feature feature : features) {
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("id", feature.getFeatureID());
            vars.put("desc", feature.getDescription());
            sender.sendMessage(translate(feature.isEnabled() ? "features.loaded.on" : "features.loaded.off", sender, vars));

            List<String> status = feature.getStatus();

            for(String message : status) {
                sender.sendMessage(message);
            }
        }
    }

    @CommandMethod("feature toggle")
    @PermissionRestriction(FEATURE_TOGGLE_PERMISSION)
    @CommandOptions("[arguments]")
    public void featureToggleCommand(OptionSet set, CommandSender sender, List<Feature> features){
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
