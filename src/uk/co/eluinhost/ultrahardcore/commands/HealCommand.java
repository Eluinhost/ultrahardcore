package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.commands.SenderType;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class HealCommand {

    public static final String HEAL_SELF_PERMISSION = "UHC.heal.self";
    public static final String HEAL_OTHER_PERMISSION = "UHC.heal.other";
    public static final String HEAL_ANNOUNCE_PERMISSION = "UHC.heal.announce";
    public static final String HEAL_ALL_PERMISSION = "UHC.heal.all";

    /**
     * Ran on /healself
     * @param request request params
     */
    @Command(trigger = "healself",
            identifier = "HealSelfCommand",
            minArgs = 0,
            maxArgs = 0,
            senders = {SenderType.PLAYER},
            permission = HEAL_SELF_PERMISSION)
    public void onHealSelfCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        player.setHealth(player.getMaxHealth());
        player.sendMessage(ChatColor.GOLD + "You healed yourself to full health");
        ServerUtil.broadcastForPermission(
                String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] Player " + player.getName() + " used a heal command to heal themselves to " + player.getMaxHealth() + " health"
                ,HEAL_ANNOUNCE_PERMISSION
        );
    }

    /**
     * Ran on /heal {name}
     * @param request request params
     */
    @Command(trigger = "heal",
            identifier = "HealCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = HEAL_OTHER_PERMISSION)
    public void onHealCommand(CommandRequest request){
        CommandSender sender = request.getSender();
        Player p = sender.getServer().getPlayer(request.getFirstArg());
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "Invalid player name " + request.getFirstArg());
            return;
        }
        p.setHealth(p.getMaxHealth());
        p.sendMessage(ChatColor.GOLD + "You were healed to full health");
        ServerUtil.broadcastForPermission(
                String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] " +
                        (sender instanceof Player ? "Player " + sender.getName() : "Console") + " healed player " + p.getName()
                , HEAL_ANNOUNCE_PERMISSION
        );
    }

    /**
     * Ran on /heal *
     * @param request request params
     */
    @Command(trigger = "*",
            identifier = "HealAllCommand",
            parentID = "HealCommand",
            permission = HEAL_OTHER_PERMISSION,
            minArgs = 0,
            maxArgs = 0)
    public void onHealAllCommand(CommandRequest request){
        CommandSender sender = request.getSender();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(p.getMaxHealth());
            p.sendMessage(ChatColor.GOLD + "You were healed to full health");
        }
        ServerUtil.broadcastForPermission(
                String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] " +
                        (sender instanceof Player ? "Player " + sender.getName() : "Console") + " healed all players"
                , HEAL_ANNOUNCE_PERMISSION
        );
    }
}
