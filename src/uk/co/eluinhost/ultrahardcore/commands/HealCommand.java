package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.commands.SenderType;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class HealCommand {

    @Command(trigger = "healself",
            identifier = "HealSelfCommand",
            minArgs = 0,
            maxArgs = 0,
            senders = {SenderType.PLAYER},
            permission = PermissionNodes.HEAL_SELF)
    public void onHealSelfCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        player.setHealth(player.getMaxHealth());
        player.sendMessage(ChatColor.GOLD + "You healed yourself to full health");
        ServerUtil.broadcastForPermission(
                String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] Player " + player.getName() + " used a heal command to heal themselves to " + player.getMaxHealth() + " health"
                ,PermissionNodes.HEAL_ANNOUNCE
        );
    }

    @Command(trigger = "heal",
            identifier = "HealCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = PermissionNodes.HEAL_OTHER)
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
                , PermissionNodes.HEAL_ANNOUNCE
        );
    }

    @Command(trigger = "*",
            identifier = "HealAllCommand",
            parentID = "HealCommand",
            permission = PermissionNodes.HEAL_ALL,
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
                , PermissionNodes.HEAL_ANNOUNCE
        );
    }
}
