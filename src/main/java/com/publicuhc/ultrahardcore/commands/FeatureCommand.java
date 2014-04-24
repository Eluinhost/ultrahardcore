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

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.routing.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FeatureCommand extends SimpleCommand {

    public static final String FEATURE_LIST_PERMISSION = "UHC.feature.list";
    public static final String FEATURE_TOGGLE_PERMISSION = "UHC.feature.toggle";

    private final FeatureManager m_featureManager;

    /**
     * feature commands
     * @param configManager the config manager
     * @param translate the translator
     * @param featureManager the feature manager
     */
    @Inject
    private FeatureCommand(Configurator configManager, Translate translate, FeatureManager featureManager){
        super(configManager, translate);
        m_featureManager = featureManager;
    }

    /**
     * @param request request params
     */
    @CommandMethod
    public void featureCommand(CommandRequest request){
        request.sendMessage(ChatColor.RED+"/feature list - List features");
        request.sendMessage(ChatColor.RED + "/feature on <featureID> - turn feature on");
        request.sendMessage(ChatColor.RED+"/feature off <featureID> - turn feature off");
    }

    /**
     * Run whenever a /feature command is run and nothing else triggers
     * @param builder the builder
     */
    @RouteInfo
    public void featureCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feature");
        builder.maxMatches(1);
    }

    /**
     * List all the features and their status
     * @param request request params
     */
    @CommandMethod
    public void featureListCommand(CommandRequest request){
        List<IFeature> features = m_featureManager.getFeatures();
        request.sendMessage(translate("features.loaded.header", request.getLocale(), "amount", String.valueOf(features.size())));
        if (features.isEmpty()) {
            request.sendMessage(translate("features.loaded.none", request.getLocale()));
        }
        for (IFeature feature : features) {
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("id", feature.getFeatureID());
            vars.put("desc", feature.getDescription());
            request.sendMessage(translate(feature.isEnabled()?"features.loaded.on":"features.loaded.off", request.getLocale(), vars));
        }
    }

    /**
     * Run on /feauture list
     * @param builder the builder
     */
    @RouteInfo
    public void featureListCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feature");
        builder.restrictPermission(FEATURE_LIST_PERMISSION);
        builder.restrictPattern(Pattern.compile("list.*"));
    }

    /**
     * Turn on a feature
     * @param request request params
     */
    @CommandMethod
    public void featureOnCommand(CommandRequest request){
        IFeature feature = m_featureManager.getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.sendMessage(translate("features.not_found", request.getLocale(), "id", request.getFirstArg()));
            return;
        }
        if(feature.isEnabled()){
            request.sendMessage(translate("features.already_enabled", request.getLocale()));
            return;
        }
        if(!feature.enableFeature()){
            request.sendMessage(translate("features.enabled_cancelled", request.getLocale()));
            return;
        }
        request.sendMessage(translate("features.enabled", request.getLocale()));
    }

    /**
     * Run on /feature on {name}
     * @param builder the builder
     */
    @RouteInfo
    public void featureOnCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feature");
        builder.restrictPermission(FEATURE_TOGGLE_PERMISSION);
        builder.restrictPattern(Pattern.compile("on [\\S]+.*"));
    }

    /**
     * Toggle a feature off
     * @param request request params
     */
    @CommandMethod
    public void onFeatureOffCommand(CommandRequest request){
        IFeature feature = m_featureManager.getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.sendMessage(translate("features.not_found", request.getLocale(), "id", request.getFirstArg()));
            return;
        }
        if(!feature.isEnabled()){
            request.sendMessage(translate("features.already_disabled", request.getLocale()));
            return;
        }
        if(!feature.disableFeature()){
            request.sendMessage(translate("features.disabled_cancelled", request.getLocale()));
            return;
        }
        request.sendMessage(translate("features.disabled", request.getLocale()));
    }

    /**
     * Run on /feature off {name}
     * @param builder the builder
     */
    @RouteInfo
    public void featureOffCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feature");
        builder.restrictPattern(Pattern.compile("off [\\S]+.*"));
        builder.restrictPermission(FEATURE_TOGGLE_PERMISSION);
    }
}
