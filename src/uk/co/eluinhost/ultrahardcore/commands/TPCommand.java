package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;

import java.util.List;

public class TPCommand {

    public static final String TP_ALL_PERMISSION = "UHC.tpall";

    /**
     * Ran on /tpp {list of players} {player/location}
     * @param request request params
     *                TODO use new methods
     */
    @Command(trigger = "tpp",
            identifier = "TeleportCommand",
            minArgs = 2,
            permission = TP_ALL_PERMISSION)
    public void onTeleportCommand(CommandRequest request){
        List<String> arguments = request.getArgs();
        Location location;
        String lastArg = request.getLastArg();
        if (lastArg.contains(",")) {
            String[] coords = lastArg.split(",");
            World w;
            if (coords.length == 3) {
                if (!(request.getSender() instanceof Player)) {
                    request.sendMessage(ChatColor.RED + "You need to specfiy a world when not a player to teleport");
                    return;
                }
                w = ((Entity) request.getSender()).getWorld();
            } else if (coords.length == 4) {
                w = Bukkit.getWorld(coords[3]);
                if (w == null) {
                    request.sendMessage(ChatColor.RED + "World " + coords[3] + " not found!");
                    return;
                }
            } else {
                request.sendMessage(ChatColor.RED + "Invalid coordinate syntax, try x,y,z[,worldname]");
                return;
            }
            int x;
            int y;
            int z;
            try {
                x = Integer.parseInt(coords[0]);
                y = Integer.parseInt(coords[1]);
                z = Integer.parseInt(coords[2]);
            } catch (NumberFormatException ignored) {
                request.sendMessage(ChatColor.RED + "Invalid numbers used for coordinates: " + coords[0] + "," + coords[1] + "," + coords[2]);
                return;
            }
            location = new Location(w, x, y, z);
        } else {
            Player p = Bukkit.getPlayer(lastArg);
            if (p == null) {
                request.sendMessage(ChatColor.RED + "Player " + lastArg + " not found!");
                return;
            }
            location = p.getLocation();
        }
        if (arguments.size() == 2 && "*".equals(request.getFirstArg())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(location);
            }
        } else {
            for (int i = 0; i < arguments.size() - 1; i++) {
                Player p = Bukkit.getPlayer(arguments.get(i));
                if (p == null) {
                    request.sendMessage(ChatColor.RED + "Player " + arguments.get(i) + " not found");
                    continue;
                }
                p.teleport(location);
            }
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + request.getSender().getName() + " teleported all players to " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getWorld().getName());
    }
}
