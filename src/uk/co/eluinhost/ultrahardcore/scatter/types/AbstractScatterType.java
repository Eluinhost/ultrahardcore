package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.scatter.PlayerTeleportMapping;
import uk.co.eluinhost.ultrahardcore.services.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterParams;

public abstract class AbstractScatterType {

    private final String m_scatterID;
    private final String m_description;

    private static final Random RANDOM = new Random();

    /**
     * Represents scatter logic
     * @param scatterID the ID for this type
     * @param description a short description
     */
    protected AbstractScatterType(String scatterID, String description){
        m_scatterID = scatterID;
        m_description = description;
    }

    /**
     * @return the scatter ID
     */
    public String getScatterID(){
        return m_scatterID;
    }

    /**
     * @return the description
     */
    public String getDescription(){
        return m_description;
    }

    //TODO look at cleaning up children methods of this
    /**
     * Get a list of scatter locations for the given parameters
     * @param params the parameters to use
     * @param amount the amount of locations to return
     * @return a list of locations that fit the logic
     * @throws WorldNotFoundException if the world provided doesn't exist
     * @throws MaxAttemptsReachedException if the max attempts to scatter was reached
     */
    public abstract List<Location> getScatterLocations(ScatterParams params, int amount) throws WorldNotFoundException, MaxAttemptsReachedException;

    /**
     * Checks if locations are too close to each other, also checks for teleports that are in progress in the scatter manager
     * @param loc the location to check
     * @param existing the list of location to check against
     * @param distance the allowed distnace between the 2
     * @return true if too close, false if none were within the distance
     */
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
        for (PlayerTeleportMapping ptm : UltraHardcore.getInstance().getScatterManager().getRemainingTeleports()) {
            if (ptm.getLocation().distanceSquared(loc) < distanceSquared) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return random
     */
    public static Random getRandom() {
        return RANDOM;
    }
}
