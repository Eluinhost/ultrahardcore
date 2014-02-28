package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class HealCommand {

    @Command(trigger = "heal", identifier = "HealCommand")
    public void onHealCommand(CommandRequest request){
        CommandSender sender = request.getSender();
        if(request.getArgs().isEmpty()){
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED + "You can only run this command as a player");
                return;
            }
            Player player = (Player) sender;
            if (sender.hasPermission(PermissionNodes.HEAL_SELF)) {
                sender.sendMessage(ChatColor.RED+"You don't have the permission "+PermissionNodes.HEAL_SELF);
                return;
            }
            player.setHealth(player.getMaxHealth());
            player.sendMessage(ChatColor.GOLD + "You healed yourself to full health");
            ServerUtil.broadcastForPermission(
                String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] Player " + player.getName() + " used a heal command to heal themselves to " + player.getMaxHealth() + " health"
                ,PermissionNodes.HEAL_ANNOUNCE
            );
            return;
        }
        Player p = sender.getServer().getPlayer(request.getFirstArg());
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "Invalid player name " + request.getFirstArg());
            return;
        }
        if (!sender.hasPermission(PermissionNodes.HEAL_OTHER)) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to heal another player (" + PermissionNodes.HEAL_OTHER + "). If you want to heal yourself and have permissions use '/heal' by itself");
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

    @Command(trigger = "*", identifier = "HealAllCommand")
    public void onHealAllCommand(CommandRequest request){
        CommandSender sender = request.getSender();
        if (!sender.hasPermission(PermissionNodes.HEAL_ALL)) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to heal all players (" + PermissionNodes.HEAL_ALL + ")");
            return;
        }
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
