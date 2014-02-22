package uk.co.eluinhost.ultrahardcore.commands;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class ClearInventoryCommand implements UHCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if ("ci".equals(command.getName())) {
            if (args.length == 0) {
                if (!sender.hasPermission(PermissionNodes.CLEAR_INVENTORY_SELF)) {
                    sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.CLEAR_INVENTORY_SELF);
                    return true;
                }
                if (!(sender instanceof HumanEntity)) {
                    sender.sendMessage(ChatColor.RED + "You can only clear your own inventory as a player!");
                    return true;
                }
                clearInventory((HumanEntity) sender);
                sender.sendMessage(ChatColor.GOLD + "Inventory cleared successfully");
                return true;
            } else {
                if (!sender.hasPermission(PermissionNodes.CLEAR_INVENTORY_OTHER)) {
                    sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.CLEAR_INVENTORY_OTHER);
                    return true;
                }
                AbstractList<String> namesNotFound = new ArrayList<String>();
                if ("*".equals(args[0])) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.hasPermission(PermissionNodes.CLEAR_INVENTORY_IMMUNE)) {
                            clearInventory(p);
                        }
                    }
                    Bukkit.broadcastMessage(ChatColor.GOLD + "All player inventories cleared by " + sender.getName());
                    return true;
                }
                for (String pname : args) {
                    Player p = Bukkit.getPlayer(pname);
                    if (p == null) {
                        namesNotFound.add(pname);
                        continue;
                    }
                    if (p.hasPermission(PermissionNodes.CLEAR_INVENTORY_IMMUNE)) {
                        sender.sendMessage(ChatColor.RED + "Player " + p.getName() + " is immune to inventory clears");
                    } else {
                        clearInventory(p);
                        p.sendMessage(ChatColor.GOLD + "Your inventory was cleared by " + sender.getName());
                    }
                }
                sender.sendMessage(ChatColor.GOLD + "All inventories cleared successfully");
                if (!namesNotFound.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    message.append(ChatColor.GOLD).append("Players not found to clear:");
                    for (String s : namesNotFound) {
                        message.append(' ').append(s);
                    }
                    sender.sendMessage(message.toString());
                }
                return true;
            }
        }
        return false;
    }

    private static void clearInventory(HumanEntity p) {
        p.getInventory().clear();
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.setItemOnCursor(new ItemStack(Material.AIR));
        InventoryView openInventory = p.getOpenInventory();
        if (openInventory.getType() == InventoryType.CRAFTING) {
            openInventory.getTopInventory().clear();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> p = ServerUtil.getOnlinePlayers();
        p.add("*");
        return p;
    }
}
