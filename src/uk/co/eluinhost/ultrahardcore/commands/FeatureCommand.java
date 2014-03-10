package uk.co.eluinhost.ultrahardcore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.features.IFeature;
import uk.co.eluinhost.features.FeatureManager;

public class FeatureCommand extends SimpleCommand {

    public static final String FEATURE_LIST_PERMISSION = "UHC.feature.list";
    public static final String FEATURE_TOGGLE_PERMISSION = "UHC.feature.toggle";

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
        CommandSender sender = request.getSender();
        List<IFeature> features = FeatureManager.getInstance().getFeatures();
        sender.sendMessage(translate("features.loaded.header").replaceAll("%amount%",String.valueOf(features.size())));
        if (features.isEmpty()) {
            sender.sendMessage(translate("features.loaded.none"));
        }
        for (IFeature feature : features) {
            String message = translate(feature.isEnabled()?"features.loaded.on":"features.loaded.off");
            sender.sendMessage(message.replaceAll("%id%",feature.getFeatureID()).replaceAll("%desc%",feature.getDescription()));
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
        IFeature feature = FeatureManager.getInstance().getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.getSender().sendMessage(translate("features.not_found").replaceAll("%id%",request.getFirstArg()));
            return;
        }
        if(feature.isEnabled()){
            request.getSender().sendMessage(translate("features.already_enabled"));
            return;
        }
        if(!feature.enableFeature()){
            request.getSender().sendMessage(translate("features.enabled_cancelled"));
            return;
        }
        request.getSender().sendMessage(translate("features.enabled"));
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
        IFeature feature = FeatureManager.getInstance().getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.getSender().sendMessage(translate("features.not_found").replaceAll("%id%",request.getFirstArg()));
            return;
        }
        if(!feature.isEnabled()){
            request.getSender().sendMessage(translate("features.already_disabled"));
            return;
        }
        if(!feature.disableFeature()){
            request.getSender().sendMessage(translate("features.disabled_cancelled"));
            return;
        }
        request.getSender().sendMessage(translate("features.disabled"));
    }
}
