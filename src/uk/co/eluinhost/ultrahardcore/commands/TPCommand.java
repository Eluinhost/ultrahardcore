package uk.co.eluinhost.ultrahardcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class TPCommand implements UHCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if ("tpp".equals(command.getName())) {
            if (!sender.hasPermission(PermissionNodes.TP_ALL)) {
                sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.TP_ALL.getName());
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. /tpp list_of_players/* playername OR /tpp list of players/* x,y,z[,worldname]");
                return true;
            }
            Location location;
            if (args[args.length - 1].contains(",")) {
                String[] coords = args[args.length - 1].split(",");
                World w;
                if (coords.length == 3) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "You need to specfiy a world when using the console to teleport");
                        return true;
                    }
                    w = ((Entity) sender).getWorld();
                } else if (coords.length == 4) {
                    w = Bukkit.getWorld(coords[3]);
                    if (w == null) {
                        sender.sendMessage(ChatColor.RED + "World " + coords[3] + " not found!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid coordinate syntax, try x,y,z[,worldname]");
                    return true;
                }
                int x;
                int y;
                int z;
                try {
                    x = Integer.parseInt(coords[0]);
                    y = Integer.parseInt(coords[1]);
                    z = Integer.parseInt(coords[2]);
                } catch (Exception ignored) {
                    sender.sendMessage(ChatColor.RED + "Invalid numbers used for coordinates: " + coords[0] + "," + coords[1] + "," + coords[2]);
                    return true;
                }
                location = new Location(w, x, y, z);
            } else {
                Player p = Bukkit.getPlayer(args[args.length - 1]);
                if (p == null) {
                    sender.sendMessage(ChatColor.RED + "Player " + args[args.length - 1] + " not found!");
                    return true;
                }
                location = p.getLocation();
            }
            if (args.length == 2 && "*".equals(args[0])) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.teleport(location);
                }
            } else {
                for (int i = 0; i < args.length - 1; i++) {
                    Player p = Bukkit.getPlayer(args[i]);
                    if (p == null) {
                        sender.sendMessage(ChatColor.RED + "Player " + args[i] + " not found");
                        continue;
                    }
                    p.teleport(location);
                }
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + sender.getName() + " teleported all players to " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getWorld().getName());
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> r = new ArrayList<String>();
        r.addAll(ServerUtil.getOnlinePlayers());
        return r;
    }

}
