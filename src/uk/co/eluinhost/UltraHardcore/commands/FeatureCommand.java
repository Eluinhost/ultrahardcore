package uk.co.eluinhost.UltraHardcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.eluinhost.UltraHardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.UltraHardcore.exceptions.FeatureStateNotChangedException;
import uk.co.eluinhost.UltraHardcore.features.FeatureManager;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;
import uk.co.eluinhost.UltraHardcore.features.UHCFeatureList;

public class FeatureCommand extends UHCCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(command.getName().equalsIgnoreCase("feature")){
			if(args.length == 0){
				sender.sendMessage(ChatColor.GRAY+"/feature toggle/on/off [featureID] to toggle features");
				sender.sendMessage(ChatColor.GRAY+"/feature list to list features");
				return true;
			}
            if(args[0].equalsIgnoreCase("list")){
				if(!sender.hasPermission(PermissionNodes.FEATURE_LIST)){
					sender.sendMessage(ChatColor.RED+"You don't have permission to view features ("+PermissionNodes.FEATURE_LIST+")");
					return true;
				}
				UHCFeatureList features = FeatureManager.getFeatures();
				sender.sendMessage(ChatColor.GOLD+"Currently loaded features ("+features.size()+"):");
				if(features.size() == 0){
					sender.sendMessage(ChatColor.GRAY+"Nothing to see here!");
				}
				for(UHCFeature feature : features){
					sender.sendMessage((feature.isEnabled()?ChatColor.GREEN+"ON ":ChatColor.RED+"OFF ")+feature.getFeatureID()+ChatColor.GRAY+" - "+feature.getDescription());
				}
				return true;
			}
            if(args.length < 2){
                sender.sendMessage(ChatColor.GRAY +"The command is unknown. Possible commands are:");
                sender.sendMessage(ChatColor.GRAY+"/feature toggle/on/off featureID");
                sender.sendMessage(ChatColor.GRAY+"/feature list");
                return true;
            }
            UHCFeature feature;
            try {
                feature = FeatureManager.getFeature(args[1]);
            } catch (FeatureIDNotFoundException e) {
                sender.sendMessage(ChatColor.RED+"The feature \""+args[1]+" was not found, use /feature list to see a list of available features");
                return true;
            }
            if(args[0].equalsIgnoreCase("toggle")){
                if(!sender.hasPermission(PermissionNodes.FEATURE_TOGGLE)){
                    sender.sendMessage(ChatColor.RED+"You don't have permission to toggle features ("+PermissionNodes.FEATURE_TOGGLE+")");
                    return true;
                }
                args[0] = feature.isEnabled() ? "off" : "on";
            }
            if(args[0].equalsIgnoreCase("on")){
                if(!sender.hasPermission(PermissionNodes.FEATURE_TOGGLE)){
                    sender.sendMessage(ChatColor.RED+"You don't have permission to change features");
                    return true;
                }
                try {
                    feature.setEnabled(true);
                } catch (FeatureStateNotChangedException e) {
                    sender.sendMessage(ChatColor.RED + "Feature " + args[1] + " is already enabled!");
                    return true;
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + "Feature " + args[1] + " is now enabled");
                return true;
            }else if(args[0].equalsIgnoreCase("off")){
                if(!sender.hasPermission(PermissionNodes.FEATURE_TOGGLE)){
                    sender.sendMessage(ChatColor.RED+"You don't have permission to change features");
                    return true;
                }
                try {
                    feature.setEnabled(false);
                } catch (FeatureStateNotChangedException e) {
                    sender.sendMessage(ChatColor.RED + "Feature " + args[1] + " is already disabled!");
                    return true;
                }
                Bukkit.broadcastMessage(ChatColor.GOLD+"Feature "+args[1]+" is now disabled");
                return true;
            }
			sender.sendMessage(ChatColor.GRAY +"The command is unknown. Possible commands are:");
			sender.sendMessage(ChatColor.GRAY+"/feature toggle/on/off featureID");
			sender.sendMessage(ChatColor.GRAY+"/feature list");
			return true;
		}
        return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String label, String[] args) {
		ArrayList<String> results = new ArrayList<String>();
		if(args.length == 1){
			results.add("toggle");
			results.add("list");
		}
		if(args.length == 2 && args[0].equalsIgnoreCase("toggle")){
			results.addAll(FeatureManager.getFeatureNames());
		}
		return results;
	}
	
}
