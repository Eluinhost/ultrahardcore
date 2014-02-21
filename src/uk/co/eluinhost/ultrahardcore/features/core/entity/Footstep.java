package uk.co.eluinhost.ultrahardcore.features.core.entity;

import org.bukkit.Location;

public class Footstep {

    private final Location m_location;
    private int m_timeRemaining;
    private final String m_playerName;

    public Footstep(Location loc, int timeToLast, String name) {
        m_playerName = name;
        m_location = loc;
        m_timeRemaining = timeToLast;
    }

    public int getTimeRemaining() {
        return m_timeRemaining;
    }

    public void decrementTimeRemaining() {
        m_timeRemaining -= 1;
    }

    public Location getLocation() {
        return m_location;
    }

    public String getName() {
        return m_playerName;
    }
}
