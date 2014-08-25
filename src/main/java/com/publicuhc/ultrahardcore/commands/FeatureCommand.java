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

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.OptionsMethod;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionParser;
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

    /**
     * @param translate the translator
     * @param featureManager the feature manager
     */
    @Inject
    private FeatureCommand(Translate translate, FeatureManager featureManager){
        super(translate);
        this.featureManager = featureManager;
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

    @CommandMethod(command = "feature list", permission = FEATURE_LIST_PERMISSION)
    public void featureListCommand(CommandRequest request)
    {
        List<Feature> features = featureManager.getFeatures();
        request.sendMessage(translate("features.loaded.header", request.getSender(), "amount", String.valueOf(features.size())));
        if (features.isEmpty()) {
            request.sendMessage(translate("features.loaded.none", request.getSender()));
        }
        for (Feature feature : features) {
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("id", feature.getFeatureID());
            vars.put("desc", feature.getDescription());
            request.sendMessage(translate(feature.isEnabled()?"features.loaded.on":"features.loaded.off", request.getSender(), vars));

            List<String> status = feature.getStatus();

            if (status != null) {
                for(String message : status) {
                    request.sendMessage(message);
                }
            }
        }
    }

    @CommandMethod(command = "feature toggle", permission = FEATURE_TOGGLE_PERMISSION)
    public void featureToggleCommand(CommandRequest request){
        List<Feature> features = (List<Feature>) request.getOptions().nonOptionArguments();

        for(Feature feature : features) {
            toggleFeature(feature, request.getSender());
        }
    }

    @OptionsMethod
    public void featureToggleCommand(OptionParser parser)
    {
        parser.nonOptions().withValuesConvertedBy(new FeatureValueConverter(featureManager));
    }

    @CommandMethod(command = "feature on", permission = FEATURE_TOGGLE_PERMISSION)
    public void featureOnCommand(CommandRequest request)
    {
        List<Feature> features = (List<Feature>) request.getOptions().nonOptionArguments();

        for(Feature feature : features) {
            turnFeatureOn(feature, request.getSender());
        }
    }

    @OptionsMethod
    public void featureOnCommand(OptionParser parser)
    {
        parser.nonOptions().withValuesConvertedBy(new FeatureValueConverter(featureManager));
    }

    @CommandMethod(command = "feature off", permission = FEATURE_TOGGLE_PERMISSION)
    public void featureOffCommand(CommandRequest request)
    {
        List<Feature> features = (List<Feature>) request.getOptions().nonOptionArguments();

        for(Feature feature : features) {
            turnFeatureOff(feature, request.getSender());
        }
    }

    @OptionsMethod
    public void featureOffCommand(OptionParser parser)
    {
        parser.nonOptions().withValuesConvertedBy(new FeatureValueConverter(featureManager));
    }
}
