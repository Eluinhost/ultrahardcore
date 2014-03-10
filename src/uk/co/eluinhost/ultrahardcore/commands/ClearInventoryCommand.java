package uk.co.eluinhost.ultrahardcore.commands;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.commands.SenderType;

public class ClearInventoryCommand extends SimpleCommand {

    public static final String CLEAR_SELF_PERMISSION = "UHC.ci.self";
    public static final String CLEAR_OTHER_PERMISSION = "UHC.ci.other";
    public static final String CLEAR_IMMUNE_PERMISSION = "UHC.ci.immune";

    /**
     * Ran on /ciself
     * @param request request params
     */
    @Command(trigger = "ciself",
            identifier = "ClearInventorySelf",
            minArgs = 0,
            maxArgs = 0,
            senders = {SenderType.PLAYER},
            permission = CLEAR_SELF_PERMISSION)
    public void onClearInventorySelf(CommandRequest request){
        clearInventory((HumanEntity) request.getSender());
        request.sendMessage(translate("ci.cleared"));
    }

    /**
     * Ran on /ci {name}*
     * @param request request params
     */
    @Command(trigger = "ci",
            identifier = "ClearInventory",
            minArgs = 1,
            permission = CLEAR_OTHER_PERMISSION)
    public void onClearInventoryCommand(CommandRequest request){
        List<String> arguments = request.getArgs();
        AbstractList<String> namesNotFound = new ArrayList<String>();
        for (String pname : arguments) {
            Player p = Bukkit.getPlayer(pname);
            if (p == null) {
                namesNotFound.add(pname);
                continue;
            }
            if (p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                request.sendMessage(translate("ci.immune").replaceAll("%name%",p.getName()));
            } else {
                clearInventory(p);
                p.sendMessage(translate("ci.tell").replaceAll("%name%",request.getSender().getName()));
            }
        }
        request.sendMessage("ci.cleared");
        if (!namesNotFound.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String s : namesNotFound) {
                message.append(' ').append(s);
            }
            request.sendMessage(translate("ci.not_found").replaceAll("%list%",message.toString()));
        }
    }

    /**
     * Ran on /ci *
     * @param request request params
     */
    @Command(trigger = "*",
            identifier = "ClearInventoryAll",
            parentID = "ClearInventory",
            minArgs = 0,
            maxArgs = 0,
            permission = CLEAR_OTHER_PERMISSION)
    public void onClearInventoryAll(CommandRequest request){
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                clearInventory(p);
            }
        }
        Bukkit.broadcastMessage(translate("ci.announce_all").replaceAll("%name%",request.getSender().getName()));
    }


    /**
     * Clears the player's inventory, armour slots, item on cursor and crafting slots
     * @param p player
     */
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
