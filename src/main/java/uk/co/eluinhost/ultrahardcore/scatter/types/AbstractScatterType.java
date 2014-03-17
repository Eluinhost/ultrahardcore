package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.scatter.Parameters;

public abstract class AbstractScatterType {

    private final String m_scatterID;
    private final String m_description;

    protected static final double X_OFFSET = 0.5d;
    protected static final double Z_OFFSET = 0.5d;
    protected static final double MATH_TAU = Math.PI * 2.0D;
    protected static final int WORLD_TOP_BLOCK = 255;

    protected static final int MAX_TRIES = 250;

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

    /**
     * Get a list of scatter locations for the given parameters
     * @param params the parameters to use
     * @param amount the amount of locations to return
     * @return a list of locations that fit the logic
     * @throws MaxAttemptsReachedException if the max attempts to scatter was reached
     */
    public abstract List<Location> getScatterLocations(Parameters params, int amount) throws MaxAttemptsReachedException;

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
        //TODO need to readd this section somewhere
        /*for (Teleporter ptm : ScatterManager.getInstance().getRemainingTeleports()) {
            if (ptm.getLocation().distanceSquared(loc) < distanceSquared) {
                return true;
            }
        }*/
        return false;
    }

    /**
     * @return random
     */
    public static Random getRandom() {
        return RANDOM;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof AbstractScatterType && ((AbstractScatterType) obj).getScatterID().equals(getScatterID());
    }

    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getScatterID()).toHashCode();
    }
}
