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
import uk.co.eluinhost.ultrahardcore.features.core.DeathDropsFeature;
import uk.co.eluinhost.ultrahardcore.features.core.entity.ItemDrop;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GiveDropCommand implements UHCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (command.getName().equals("givedrops")) {
            if (!sender.hasPermission(PermissionNodes.GIVE_DROPS)) {
                sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.GIVE_DROPS);
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Syntax: /givedrops groupname */player1 player2 player3");
                return true;
            }
            List<Player> players = new ArrayList<Player>();
            List<String> not_found = new ArrayList<String>();
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("*")) {
                    players.clear();
                    not_found.clear();
                    players.addAll(Arrays.asList(Bukkit.getOnlinePlayers()));
                    break;
                }
                Player p = Bukkit.getPlayer(args[i]);
                if (p == null) {
                    not_found.add(args[i]);
                } else {
                    players.add(p);
                }
            }
            if (players.size() == 0) {
                sender.sendMessage(ChatColor.RED + "No valid players given");
                return true;
            }
            try {
                List<ItemDrop> drops = ((DeathDropsFeature) UltraHardcore.getInstance().getFeatureManager().getFeatureByID("DeathDrops")).getItemDropForGroup(args[0]);
                if (drops.size() == 0) {
                    sender.sendMessage(ChatColor.RED + "Could not find any items defined with the group name " + args[0]);
                    return true;
                }
                List<ItemStack> items = new ArrayList<ItemStack>();
                for (ItemDrop i : drops) {
                    ItemStack is = i.getItemStack();
                    if (is != null) {
                        items.add(is);
                    }
                }
                for (Player p : players) {
                    HashMap<Integer, ItemStack> i = p.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
                    if (i.keySet().size() != 0) {
                        Location l = p.getEyeLocation();
                        for (ItemStack is : i.values()) {
                            l.getWorld().dropItem(l, is);
                        }
                    }
                }
                if (not_found.size() != 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.GOLD);
                    sb.append("Could not find the following players:");
                    for (String s : not_found) {
                        sb.append(" ");
                        sb.append(s);
                        sb.append(",");
                    }
                    sb.setLength(sb.length() - 1);
                    sb.append(". For all other players items were added to inventories/dropped nearby");
                    sender.sendMessage(sb.toString());
                } else {
                    sender.sendMessage(ChatColor.GOLD + "All items added to inventories/dropped nearby");
                }
                return true;
            } catch (FeatureIDNotFoundException e) {
                sender.sendMessage(ChatColor.RED + "The module DeathDrops was not loaded for some reason, check console on startup to check for errors");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String label, String[] args) {
        List<String> p = new ArrayList<String>();
        if (args.length > 1) {
            p.add("*");
            p.addAll(ServerUtil.getOnlinePlayers());
            return p;
        }
        if (args.length == 1) {
            try {
                return ((DeathDropsFeature) UltraHardcore.getInstance().getFeatureManager().getFeatureByID("DeathDrops")).getItemDropGroups();
            } catch (FeatureIDNotFoundException e) {
                Bukkit.getLogger().severe("DeathDrops module is not loaded, check startup for error");
                return p;
            }
        }
        return p;
    }
}
