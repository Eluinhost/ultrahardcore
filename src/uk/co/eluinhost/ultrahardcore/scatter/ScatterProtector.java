package uk.co.eluinhost.ultrahardcore.scatter;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//TODO use weakhashmap with player objects
public class ScatterProtector implements Listener {
    private static final HashMap<String, Location> PROTECTED_PLAYERS = new HashMap<String, Location>();

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {
        if (PROTECTED_PLAYERS.containsKey(event.getPlayer().getName())) {
            Player p = event.getPlayer();
            Location newLocation = event.getTo();
            Location location = PROTECTED_PLAYERS.get(p.getName());

            //Stop if moved over 1 square
            if (distanceXZ(location, newLocation) >= 1.0 || location.getY() < newLocation.getY()) {
                PROTECTED_PLAYERS.remove(p.getName());
                return;
            }
            newLocation.setY(location.getY());
            event.setTo(newLocation);
        }
    }

    private static double distanceXZ(Location l1, Location l2) {
        return Math.abs(l1.getX() - l2.getX()) + Math.abs(l1.getZ() - l2.getZ());
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent ede) {
        if (ede.getEntity() instanceof Player) {
            //noinspection OverlyStrongTypeCast
            if (PROTECTED_PLAYERS.containsKey(((Player) ede.getEntity()).getName())) {
                switch (ede.getCause()) {
                    case SUFFOCATION:
                    case FALL:
                    case VOID:
                        ede.setCancelled(true);
                        break;
                    default:
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent pqe) {
        PROTECTED_PLAYERS.remove(pqe.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent pke) {
        PROTECTED_PLAYERS.remove(pke.getPlayer().getName());
    }

    public void addPlayer(String name, Location loc) {
        PROTECTED_PLAYERS.put(name, loc);
    }
}
