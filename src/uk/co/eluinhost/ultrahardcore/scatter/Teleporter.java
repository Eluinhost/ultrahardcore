package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;

public abstract class Teleporter {

    private final WeakReference<Player> m_player;
    private int m_amountTried = 0;
    private final Location m_location;
    private final String m_team;

    private static final Vector Y_OFFSET = new Vector(0,2,0);

    /**
     * @param player the player to teleport
     * @param loc the location to teleport to (2 is added to the Y coordinate)
     * @param teamName the team name to teleport as
     */
    protected Teleporter(Player player, Location loc, String teamName) {
        m_player = new WeakReference<Player>(player);
        m_location = loc.add(Y_OFFSET);
        m_team = teamName;
    }

    //TODO amount tried logic goes where?
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
     * Get the player to be teleported, null if player was GCd
     * @return Player
     */
    public Player getPlayer() {
        return m_player.get();
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

    /**
     * Try to process this teleport
     */
    public abstract void teleport();

}
