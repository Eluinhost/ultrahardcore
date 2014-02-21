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
 * <p/>
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
            if (ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.DEATH_MESSAGES_SUPPRESSED)) {
                if (pde.getEntity().hasPermission(PermissionNodes.DEATH_MESSAGE_SUPPRESSED)) {
                    pde.setDeathMessage("");
                }
            } else {
                if (pde.getEntity().hasPermission(PermissionNodes.DEATH_MESSAGE_AFFIXES)) {
                    String format = ChatColor.translateAlternateColorCodes('&', ConfigHandler.getConfig(ConfigHandler.MAIN).getString(ConfigNodes.DEATH_MESSAGES_FORMAT));
                    format = format.replaceAll("%message", pde.getDeathMessage());
                    format = format.replaceAll("%player", pde.getEntity().getName());
                    Location loc = pde.getEntity().getLocation();
                    format = format.replaceAll("%coords", locationString(loc));
                    pde.setDeathMessage(format);
                }
            }
        }
    }

    private String locationString(Location loc) {
        return "x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ();
    }

    @Override
    public void enableFeature() {
    }

    @Override
    public void disableFeature() {
    }
}
