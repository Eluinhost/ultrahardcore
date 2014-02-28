package uk.co.eluinhost.ultrahardcore.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class HealCommandOld implements UHCCommand {

    //todo needs cleaning up
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("heal".equals(command.getName())) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (p.hasPermission(PermissionNodes.HEAL_SELF)) {
                        p.setHealth(p.getMaxHealth());
                        p.sendMessage(ChatColor.GOLD + "You healed yourself to full health");
                        ServerUtil.broadcastForPermission(
                                String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] Player " + p.getName() + " used a heal command to heal themselves to " + p.getMaxHealth() / 2 + " hearts"
                                , PermissionNodes.HEAL_ANNOUNCE
                        );
                    } else {
                        p.sendMessage(ChatColor.RED + "You don't have the permission to heal yourself (" + PermissionNodes.HEAL_SELF + ")");
                    }
                    return true;
                } else {
                    sender.sendMessage("You can only use /heal to heal yourself as a player use '/heal <player>' to heal a specific player");
                    return true;
                }
            } else {
                if ("*".equalsIgnoreCase(args[0])) {
                    if (!sender.hasPermission(PermissionNodes.HEAL_ALL)) {
                        sender.sendMessage(ChatColor.RED + "You don't have the permission to heal all players (" + PermissionNodes.HEAL_ALL + ")");
                        return true;
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
                    return true;
                }
                Player p = sender.getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid player name " + args[0]);
                    return true;
                }
                if (!sender.hasPermission(PermissionNodes.HEAL_OTHER)) {
                    sender.sendMessage(ChatColor.RED + "You don't have the permission to heal another player (" + PermissionNodes.HEAL_OTHER + "). If you want to heal yourself and have permissions use '/heal' by itself");
                    return true;
                }
                p.setHealth(p.getMaxHealth());
                p.sendMessage(ChatColor.GOLD + "You were healed to full health");
                ServerUtil.broadcastForPermission(
                        String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] " +
                                (sender instanceof Player ? "Player " + sender.getName() : "Console") + " healed player " + p.getName()
                        , PermissionNodes.HEAL_ANNOUNCE
                );
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        List<String> p = ServerUtil.getOnlinePlayers();
        p.add("*");
        return p;
    }
}
