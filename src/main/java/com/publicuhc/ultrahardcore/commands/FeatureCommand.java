package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.features.FeatureManager;
import com.publicuhc.features.IFeature;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Ran on /feature
     * @param request request params
     */
    @Command(trigger = "feature",
            identifier = "FeatureCommand")
    public void onFeatureCommand(CommandRequest request){
        //TODO show syntax?
    }

    /**
     * Ran on /feature list
     * @param request request params
     */
    @Command(trigger = "list",
            identifier = "FeatureListCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = FEATURE_LIST_PERMISSION,
            parentID = "FeatureCommand")
    public void onFeatureListCommand(CommandRequest request){
        List<IFeature> features = m_featureManager.getFeatures();
        request.sendMessage(translate("features.loaded.header", locale(request.getSender()), "amount", String.valueOf(features.size())));
        if (features.isEmpty()) {
            request.sendMessage(translate("features.loaded.none", locale(request.getSender())));
        }
        for (IFeature feature : features) {
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("id", feature.getFeatureID());
            vars.put("desc", feature.getDescription());
            request.sendMessage(translate(feature.isEnabled()?"features.loaded.on":"features.loaded.off", locale(request.getSender()), vars));
        }
    }

    /**
     * Ran on /feature on {name}
     * @param request request params
     */
    @Command(trigger = "on",
            identifier = "FeatureOnCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = FEATURE_TOGGLE_PERMISSION,
            parentID = "FeatureCommand")
    public void onFeatureOnCommand(CommandRequest request){
        IFeature feature = m_featureManager.getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.sendMessage(translate("features.not_found", locale(request.getSender()), "id", request.getFirstArg()));
            return;
        }
        if(feature.isEnabled()){
            request.sendMessage(translate("features.already_enabled", locale(request.getSender())));
            return;
        }
        if(!feature.enableFeature()){
            request.sendMessage(translate("features.enabled_cancelled", locale(request.getSender())));
            return;
        }
        request.sendMessage(translate("features.enabled", locale(request.getSender())));
    }

    /**
     * Ran on /feature off {name}
     * @param request request params
     */
    @Command(trigger = "off",
            identifier = "FeatureOffCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = FEATURE_TOGGLE_PERMISSION,
            parentID = "FeatureCommand")
    public void onFeatureOffCommand(CommandRequest request){
        IFeature feature = m_featureManager.getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.sendMessage(translate("features.not_found", locale(request.getSender()), "id", request.getFirstArg()));
            return;
        }
        if(!feature.isEnabled()){
            request.sendMessage(translate("features.already_disabled", locale(request.getSender())));
            return;
        }
        if(!feature.disableFeature()){
            request.sendMessage(translate("features.disabled_cancelled", locale(request.getSender())));
            return;
        }
        request.sendMessage(translate("features.disabled", locale(request.getSender())));
    }
}
