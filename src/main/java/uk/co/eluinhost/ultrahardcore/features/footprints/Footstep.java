package uk.co.eluinhost.ultrahardcore.features.footprints;

import org.bukkit.Location;

public class Footstep {

    private final Location m_location;
    private int m_timeRemaining;
    private final String m_playerName;

    /**
     * Make a new footstep
     * @param loc the location to display it
     * @param timeToLast the amount of footstep ticks to last
     * @param name the name of the player that left the footprint
     */
    public Footstep(Location loc, int timeToLast, String name) {
        m_playerName = name;
        m_location = loc;
        m_timeRemaining = timeToLast;
    }

    /**
     * @return amount of time left
     */
    public int getTimeRemaining() {
        return m_timeRemaining;
    }

    /**
     * lower the amount of time left for the footprint to remain
     */
    public void decrementTimeRemaining() {
        m_timeRemaining -= 1;
    }

    /**
     * @return the location the footprint displays at
     */
    public Location getLocation() {
        return m_location;
    }

    /**
     * @return the name of the player who left the footprint
     */
    public String getName() {
        return m_playerName;
    }
}
