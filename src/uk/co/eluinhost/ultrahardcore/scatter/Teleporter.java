package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;

public abstract class Teleporter {

    private final WeakReference<Player> m_player;
    private Location m_location;
    private String m_team = null;

    private static final Vector Y_OFFSET = new Vector(0,2,0);

    /**
     * @param player the player to teleport
     * @param loc the location to teleport to (2 is added to the Y coordinate)
     */
    protected Teleporter(Player player, Location loc) {
        m_player = new WeakReference<Player>(player);
        m_location = loc.add(Y_OFFSET);
    }

    /**
     * Get the player to be teleported, null if player was GCd
     * @return Player
     */
    public Player getPlayer() {
        return m_player.get();
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
     * @return true if teleport went through, false if player object wasn't found
     */
    public abstract boolean teleport();

    /**
     * @param team the team name to scatter with
     */
    public void setTeam(String team) {
        m_team = team;
    }

    /**
     * @return the team name to scatter with
     */
    public String getTeam(){
        return m_team;
    }

    /**
     * @param location the location to teleport to, 2 is added to Y
     */
    public void setLocation(Location location) {
        m_location = location.add(Y_OFFSET);
    }

    /**
     * @return the location to teleport to
     */
    public Location getLocation(){
        return m_location;
    }
}
