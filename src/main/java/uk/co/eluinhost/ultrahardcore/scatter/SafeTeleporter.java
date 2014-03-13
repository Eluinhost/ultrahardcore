package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SafeTeleporter extends Teleporter {

    private final ScatterManager m_scatterManager;

    /**
     * @param player   the player to teleport
     * @param loc      the location to teleport to (2 is added to the Y coordinate)
     */
    public SafeTeleporter(Player player, Location loc, ScatterManager manager) {
        super(player, loc);
        m_scatterManager = manager;
    }

    @Override
    public boolean teleport() {
        Player player = getPlayer();
        if(player == null){
            return false;
        }
        Location location = getLocation();
        m_scatterManager.teleportSafe(player,location);
        player.sendMessage(ChatColor.GOLD + "You were teleported "
                + (getTeamName() == null ? "solo" : "with team " + getTeamName())
                + " to " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
        return true;
    }
}
