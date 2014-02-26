package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.services.ScatterManager;

public class SafeTeleporter extends Teleporter {

    /**
     * @param player   the player to teleport
     * @param loc      the location to teleport to (2 is added to the Y coordinate)
     * @param teamName the team name to teleport as
     */
    public SafeTeleporter(Player player, Location loc, String teamName) {
        super(player, loc, teamName);
    }

    @Override
    public void teleport() {
        Player player = getPlayer();
        Location location = getLocation();
        if(player != null){
            ScatterManager.getInstance().teleportSafe(player,location);
            player.sendMessage(ChatColor.GOLD + "You were teleported "
                    + (getTeamName() == null ? "solo" : "with team " + getTeamName())
                    + " to " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
        }
    }
}
