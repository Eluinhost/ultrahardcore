package uk.co.eluinhost.ultrahardcore.commands;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;

public class ClearInventoryCommand {

    @Command(trigger = "ci", identifier = "ClearInventory")
    public void onClearInventoryCommand(CommandRequest request){
        List<String> arguments = request.getArgs();
        CommandSender sender = request.getSender();

        if (arguments.isEmpty()) {
            if (!sender.hasPermission(PermissionNodes.CLEAR_INVENTORY_SELF)) {
                sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.CLEAR_INVENTORY_SELF);
                return;
            }
            if (!(sender instanceof HumanEntity)) {
                sender.sendMessage(ChatColor.RED + "You can only clear your own inventory as a player!");
                return;
            }
            clearInventory((HumanEntity) sender);
            sender.sendMessage(ChatColor.GOLD + "Inventory cleared successfully");
            return;
        }

        if (!sender.hasPermission(PermissionNodes.CLEAR_INVENTORY_OTHER)) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.CLEAR_INVENTORY_OTHER);
            return;
        }
        AbstractList<String> namesNotFound = new ArrayList<String>();
        for (String pname : arguments) {
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
    }

    @Command(trigger = "*", identifier = "ClearInventoryAll")
    public void onClearInventoryAll(CommandRequest request){
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(PermissionNodes.CLEAR_INVENTORY_IMMUNE)) {
                clearInventory(p);
            }
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "All player inventories cleared by " + request.getSender().getName());
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
}
