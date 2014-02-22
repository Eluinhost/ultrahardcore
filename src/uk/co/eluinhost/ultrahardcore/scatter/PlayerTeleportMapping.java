package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.Location;

public class PlayerTeleportMapping {

    private final String m_playerName;
    private int m_amountTried = 0;
    private final Location m_location;
    private final String m_team;

    public PlayerTeleportMapping(String name, Location loc, String teamName) {
        m_playerName = name;
        m_location = loc;
        m_team = teamName;
    }

    public int getAmountTried() {
        return m_amountTried;
    }

    public void incrementAmountTried() {
        m_amountTried++;
    }

    public String getPlayerName() {
        return m_playerName;
    }

    public Location getLocation() {
        return m_location;
    }

    public String getTeamName() {
        return m_team;
    }

}
