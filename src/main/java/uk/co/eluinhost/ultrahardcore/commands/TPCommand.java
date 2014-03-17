package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.configuration.ConfigManager;

import java.util.List;

public class TPCommand extends SimpleCommand {

    public static final String TP_ALL_PERMISSION = "UHC.tpall";

    public TPCommand(ConfigManager configManager) {
        super(configManager);
    }

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
                    request.sendMessage(translate("teleport.non_player_world"));
                    return;
                }
                w = ((Entity) request.getSender()).getWorld();
            } else if (coords.length == 4) {
                w = Bukkit.getWorld(coords[3]);
                if (w == null) {
                    request.sendMessage(translate("teleport.invalid.world"));
                    return;
                }
            } else {
                request.sendMessage(translate("teleport.invalid.coords"));
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
                request.sendMessage(translate("teleport.invalid.coords"));
                return;
            }
            location = new Location(w, x, y, z);
        } else {
            Player p = Bukkit.getPlayer(lastArg);
            if (p == null) {
                request.sendMessage(translate("teleport.invalid.player").replaceAll("%name%",lastArg));
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
                    request.sendMessage(translate("teleport.invalid.player").replaceAll("%name%",arguments.get(i)));
                    continue;
                }
                p.teleport(location);
            }
        }
    }
}
