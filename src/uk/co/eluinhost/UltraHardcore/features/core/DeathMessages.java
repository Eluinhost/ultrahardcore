package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;


/**
 * DeathMessages
 * Handles changes to death messages on death
 *
 * @author ghowden
 */
public class DeathMessages extends UHCFeature {

    public DeathMessages(boolean enabled) {
        super("DeathMessages", enabled);
        setDescription("Adds a prefix/suffix to all player deaths");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (isEnabled()) {
            //if death message suppression is on
            if (ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.DEATH_MESSAGES_SUPPRESSED)) {
                //and the players death messages are suppressed
                if (pde.getEntity().hasPermission(PermissionNodes.DEATH_MESSAGE_SUPPRESSED)) {
                    //set to nothing
                    pde.setDeathMessage("");
                }
                return;
            }
            //if there is an affix for the player
            if (pde.getEntity().hasPermission(PermissionNodes.DEATH_MESSAGE_AFFIXES)) {
                //grab format from config file
                String format = ChatColor.translateAlternateColorCodes('&', ConfigHandler.getConfig(ConfigHandler.MAIN).getString(ConfigNodes.DEATH_MESSAGES_FORMAT));

                //replace vars
                format = format.replaceAll("%message", pde.getDeathMessage());
                format = format.replaceAll("%player", pde.getEntity().getName());
                Location loc = pde.getEntity().getLocation();
                format = format.replaceAll("%coords", locationString(loc));

                //set the new message
                pde.setDeathMessage(format);
            }
        }
    }

    /**
     * Returns string in the format x:X y:Y z:Z
     * @param loc Location
     * @return String
     */
    private static String locationString(Location loc) {
        return "x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ();
    }

    @Override
    public void enableFeature() {
    }

    @Override
    public void disableFeature() {
    }
}
