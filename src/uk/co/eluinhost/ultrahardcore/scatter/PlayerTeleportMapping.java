package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.Location;

//TODO add teleport logic into here?
public class PlayerTeleportMapping {

    private final String m_playerName;
    private int m_amountTried;
    private final Location m_location;
    private final String m_team;

    public PlayerTeleportMapping(String name, Location loc, String teamName) {
        m_playerName = name;
        m_location = loc;
        m_team = teamName;
    }

    /**
     * Get the amount of times this mapping has been attempted
     * @return int
     */
    public int getAmountTried() {
        return m_amountTried;
    }

    /**
     * Increase the amount of times tried
     */
    public void incrementAmountTried() {
        m_amountTried++;
    }

    /**
     * Get the name of the player to teleport
     * @return String
     */
    public String getPlayerName() {
        return m_playerName;
    }

    /**
     * Get the location to teleport to
     * @return Location
     */
    public Location getLocation() {
        return m_location;
    }

    /**
     * Get the name of the team to teleport as
     * @return String
     */
    public String getTeamName() {
        return m_team;
    }

}
