package uk.co.eluinhost.ultrahardcore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.ultrahardcore.features.IUHCFeature;
import uk.co.eluinhost.ultrahardcore.features.FeatureManager;

public class FeatureCommand {

    public static final String FEATURE_LIST_PERMISSION = "UHC.feature.list";
    public static final String FEATURE_TOGGLE_PERMISSION = "UHC.feature.toggle";

    @Command(trigger = "feature",
            identifier = "FeatureCommand")
    public void onFeatureCommand(CommandRequest request){
        //TODO show syntax?
    }

    @Command(trigger = "list",
            identifier = "FeatureListCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = FEATURE_LIST_PERMISSION,
            parentID = "FeatureCommand")
    public void onFeatureListCommand(CommandRequest request){
        CommandSender sender = request.getSender();
        List<IUHCFeature> features = FeatureManager.getInstance().getFeatures();
        sender.sendMessage(ChatColor.GOLD + "Currently loaded features (" + features.size() + "):");
        if (features.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "No features loaded!");
        }
        for (IUHCFeature feature : features) {
            sender.sendMessage((feature.isEnabled() ? ChatColor.GREEN + "ON " : ChatColor.RED + "OFF ") + feature.getFeatureID() + ChatColor.GRAY + " - " + feature.getDescription());
        }
    }

    @Command(trigger = "on",
            identifier = "FeatureOnCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = FEATURE_TOGGLE_PERMISSION,
            parentID = "FeatureCommand")
    public void onFeatureOnCommand(CommandRequest request){
        IUHCFeature feature = FeatureManager.getInstance().getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.getSender().sendMessage(ChatColor.RED + "The feature \"" + request.getFirstArg() + " was not found, use /feature list to see a list of available features");
            return;
        }
        if(feature.isEnabled()){
            request.getSender().sendMessage(ChatColor.RED + "The feature \"" + request.getFirstArg() + " is already enabled!");
            return;
        }
        if(!feature.enableFeature()){
            request.getSender().sendMessage(ChatColor.RED+"Failed to enable the feature, event was cancelled");
            return;
        }
        request.getSender().sendMessage(ChatColor.GOLD+"Feature enabled");
    }

    @Command(trigger = "off",
            identifier = "FeatureOffCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = FEATURE_TOGGLE_PERMISSION,
            parentID = "FeatureCommand")
    public void onFeatureOffCommand(CommandRequest request){
        IUHCFeature feature = FeatureManager.getInstance().getFeatureByID(request.getFirstArg());
        if(null == feature){
            request.getSender().sendMessage(ChatColor.RED + "The feature \"" + request.getFirstArg() + " was not found, use /feature list to see a list of available features");
            return;
        }
        if(!feature.isEnabled()){
            request.getSender().sendMessage(ChatColor.RED + "The feature \"" + request.getFirstArg() + " is already disabled!");
            return;
        }
        if(!feature.disableFeature()){
            request.getSender().sendMessage(ChatColor.RED+"Failed to disable the feature, event was cancelled");
            return;
        }
        request.getSender().sendMessage(ChatColor.GOLD+"Feature disabled");
    }
}
