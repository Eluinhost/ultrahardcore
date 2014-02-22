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

public abstract class AbstractScatterType {

    private final String m_scatterID;
    private final String m_description;

    private static final Random RANDOM = new Random();

    protected AbstractScatterType(String scatterID, String description){
        m_scatterID = scatterID;
        m_description = description;
    }

    public String getScatterID(){
        return m_scatterID;
    }

    public String getDescription(){
        return m_description;
    }

    //TODO look at cleaning up children methods of this
    public abstract List<Location> getScatterLocations(ScatterParams params, int amount) throws WorldNotFoundException, MaxAttemptsReachedException;

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
