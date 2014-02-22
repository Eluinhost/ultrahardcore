package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.scatter.PlayerTeleportMapping;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterParams;

public abstract class ScatterType {

    public abstract String getScatterName();

    public abstract String getDescription();

    public abstract List<Location> getScatterLocations(ScatterParams params, int amount) throws WorldNotFoundException, MaxAttemptsReachedException;

    private static final Random RANDOM = new Random();

    protected static boolean isLocationTooClose(Location loc, Iterable<Location> existing, Double distance) {
        Double distanceSquared = distance * distance;
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                if (p.getLocation().distanceSquared(loc) < distanceSquared) {
                    return true;
                }
            } catch (IllegalArgumentException ignored) {}
        }
        for (Location location : existing) {
            if (location.distanceSquared(loc) < distanceSquared) {
                return true;
            }
        }
        for (PlayerTeleportMapping ptm : ScatterManager.getRemainingTeleports()) {
            if (ptm.getLocation().distanceSquared(loc) < distanceSquared) {
                return true;
            }
        }
        return false;
    }

    public static Random getRandom() {
        return RANDOM;
    }
}
