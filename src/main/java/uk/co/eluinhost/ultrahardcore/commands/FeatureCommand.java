package uk.co.eluinhost.ultrahardcore.commands;

import com.google.inject.Inject;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.features.IFeature;

import java.util.List;

public class FeatureCommand extends SimpleCommand {

    public static final String FEATURE_LIST_PERMISSION = "UHC.feature.list";
    public static final String FEATURE_TOGGLE_PERMISSION = "UHC.feature.toggle";

    private final FeatureManager m_featureManager;

    /**
     * feature commands
     * @param configManager the config manager
     * @param featureManager the feature manager
     */
    @Inject
    private FeatureCommand(ConfigManager configManager, FeatureManager featureManager){
        super(configManager);
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
        request.sendMessage(translate("features.loaded.header").replaceAll("%amount%",String.valueOf(features.size())));
        if (features.isEmpty()) {
            request.sendMessage(translate("features.loaded.none"));
        }
        for (IFeature feature : features) {
            String message = translate(feature.isEnabled()?"features.loaded.on":"features.loaded.off");
            request.sendMessage(message.replaceAll("%id%",feature.getFeatureID()).replaceAll("%desc%",feature.getDescription()));
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
            request.sendMessage(translate("features.not_found").replaceAll("%id%",request.getFirstArg()));
            return;
        }
        if(feature.isEnabled()){
            request.sendMessage(translate("features.already_enabled"));
            return;
        }
        if(!feature.enableFeature()){
            request.sendMessage(translate("features.enabled_cancelled"));
            return;
        }
        request.sendMessage(translate("features.enabled"));
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
            request.sendMessage(translate("features.not_found").replaceAll("%id%",request.getFirstArg()));
            return;
        }
        if(!feature.isEnabled()){
            request.sendMessage(translate("features.already_disabled"));
            return;
        }
        if(!feature.disableFeature()){
            request.sendMessage(translate("features.disabled_cancelled"));
            return;
        }
        request.sendMessage(translate("features.disabled"));
    }
}
