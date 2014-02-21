package uk.co.eluinhost.ultrahardcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.features.FeatureManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class FeatureCommand extends UHCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("feature".equalsIgnoreCase(command.getName())) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GRAY + "/feature toggle/on/off [featureID] to toggle features");
                sender.sendMessage(ChatColor.GRAY + "/feature list to list features");
                return true;
            }
            if ("list".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(PermissionNodes.FEATURE_LIST)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to view features (" + PermissionNodes.FEATURE_LIST + ")");
                    return true;
                }
                List<UHCFeature> features = FeatureManager.getFeatures();
                sender.sendMessage(ChatColor.GOLD + "Currently loaded features (" + features.size() + "):");
                if (features.isEmpty()) {
                    sender.sendMessage(ChatColor.GRAY + "Nothing to see here!");
                }
                for (UHCFeature feature : features) {
                    sender.sendMessage((feature.isEnabled() ? ChatColor.GREEN + "ON " : ChatColor.RED + "OFF ") + feature.getFeatureID() + ChatColor.GRAY + " - " + feature.getDescription());
                }
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.GRAY + "The command is unknown. Possible commands are:");
                sender.sendMessage(ChatColor.GRAY + "/feature toggle/on/off featureID");
                sender.sendMessage(ChatColor.GRAY + "/feature list");
                return true;
            }
            UHCFeature feature;
            try {
                feature = FeatureManager.getFeatureByID(args[1]);
            } catch (FeatureIDNotFoundException ignored) {
                sender.sendMessage(ChatColor.RED + "The feature \"" + args[1] + " was not found, use /feature list to see a list of available features");
                return true;
            }
            if ("toggle".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(PermissionNodes.FEATURE_TOGGLE)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to toggle features (" + PermissionNodes.FEATURE_TOGGLE + ")");
                    return true;
                }
                args[0] = feature.isEnabled() ? "off" : "on";
            }
            if ("on".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(PermissionNodes.FEATURE_TOGGLE)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to change features");
                    return true;
                }
                boolean turnedOn = feature.enableFeature();
                if (!turnedOn) {
                    sender.sendMessage(ChatColor.RED + "Feature " + args[1] + " is already enabled or loading was cancelled by another plugin");
                    return true;
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + "Feature " + args[1] + " is now enabled");
                return true;
            }
            if ("off".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(PermissionNodes.FEATURE_TOGGLE)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to change features");
                    return true;
                }
                boolean turnedOff = feature.disableFeature();
                if (!turnedOff) {
                    sender.sendMessage(ChatColor.RED + "Feature " + args[1] + " is already disabled, or disabling was cancelled by a plugin");
                    return true;
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + "Feature " + args[1] + " is now disabled");
                return true;
            }
            sender.sendMessage(ChatColor.GRAY + "The command is unknown. Possible commands are:");
            sender.sendMessage(ChatColor.GRAY + "/feature toggle/on/off featureID");
            sender.sendMessage(ChatColor.GRAY + "/feature list");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<String>();
        if (args.length == 1) {
            results.add("toggle");
            results.add("list");
        }
        if (args.length == 2 && "toggle".equalsIgnoreCase(args[0])) {
            results.addAll(FeatureManager.getFeatureNames());
        }
        return results;
    }

}
