package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.features.FeatureIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.features.deathdrops.DeathDropsFeature;
import uk.co.eluinhost.ultrahardcore.features.deathdrops.ItemDrop;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

import java.util.*;

public class GiveDropCommand implements UHCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("givedrops".equals(command.getName())) {
            if (!sender.hasPermission(PermissionNodes.GIVE_DROPS)) {
                sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.GIVE_DROPS);
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Syntax: /givedrops groupname */player1 player2 player3");
                return true;
            }
            Collection<Player> players = new ArrayList<Player>();
            Collection<String> notFound = new ArrayList<String>();
            for (int i = 1; i < args.length; i++) {
                if ("*".equals(args[i])) {
                    players.clear();
                    notFound.clear();
                    players.addAll(Arrays.asList(Bukkit.getOnlinePlayers()));
                    break;
                }
                Player p = Bukkit.getPlayer(args[i]);
                if (p == null) {
                    notFound.add(args[i]);
                } else {
                    players.add(p);
                }
            }
            if (players.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No valid players given");
                return true;
            }
            try {
                List<ItemDrop> drops = ((DeathDropsFeature) UltraHardcore.getInstance().getFeatureManager().getFeatureByID("DeathDrops")).getItemDropForGroup(args[0]);
                if (drops.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "Could not find any items defined with the group name " + args[0]);
                    return true;
                }
                List<ItemStack> items = new ArrayList<ItemStack>();
                for (ItemDrop itemDrop : drops) {
                    ItemStack is = itemDrop.getItemStack();
                    if (is != null) {
                        items.add(is);
                    }
                }
                for (Player p : players) {
                    HashMap<Integer, ItemStack> itemStacks = p.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
                    if (!itemStacks.keySet().isEmpty()) {
                        Location eyeLocation = p.getEyeLocation();
                        for (ItemStack is : itemStacks.values()) {
                            eyeLocation.getWorld().dropItem(eyeLocation, is);
                        }
                    }
                }
                if (notFound.isEmpty()) {
                    sender.sendMessage(ChatColor.GOLD + "All items added to inventories/dropped nearby");
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.GOLD);
                    sb.append("Could not find the following players:");
                    for (String s : notFound) {
                        sb.append(" ");
                        sb.append(s);
                        sb.append(",");
                    }
                    sb.setLength(sb.length() - 1);
                    sb.append(". For all other players items were added to inventories/dropped nearby");
                    sender.sendMessage(sb.toString());
                }
                return true;
            } catch (FeatureIDNotFoundException ignored) {
                sender.sendMessage(ChatColor.RED + "The module DeathDrops was not loaded for some reason, check console on startup to check for errors");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> p = new ArrayList<String>();
        if (args.length > 1) {
            p.add("*");
            p.addAll(ServerUtil.getOnlinePlayers());
            return p;
        }
        if (args.length == 1) {
            try {
                return ((DeathDropsFeature) UltraHardcore.getInstance().getFeatureManager().getFeatureByID("DeathDrops")).getItemDropGroups();
            } catch (FeatureIDNotFoundException ignored) {
                Bukkit.getLogger().severe("DeathDrops module is not loaded, check startup for error");
                return p;
            }
        }
        return p;
    }
}
