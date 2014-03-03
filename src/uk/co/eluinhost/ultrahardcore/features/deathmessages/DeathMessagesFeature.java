package uk.co.eluinhost.ultrahardcore.features.deathmessages;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * DeathMessagesFeature
 * Handles changes to death messages on death
 *
 * @author ghowden
 */
public class DeathMessagesFeature extends UHCFeature {

    public static final String BASE_MESSAGES = BASE_PERMISSION + "death_messages.";
    public static final String DEATH_MESSAGE_SUPPRESSED = BASE_MESSAGES + "remove";
    public static final String DEATH_MESSAGE_AFFIXES = BASE_MESSAGES + "affixes";

    public DeathMessagesFeature() {
        super("DeathMessages","Adds a prefix/suffix to all player deaths");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (isEnabled()) {
            //if death message suppression is on
            if (ConfigManager.getInstance().getConfig().getBoolean(ConfigNodes.DEATH_MESSAGES_SUPPRESSED)) {
                //and the players death messages are suppressed
                if (pde.getEntity().hasPermission(DEATH_MESSAGE_SUPPRESSED)) {
                    //set to nothing
                    pde.setDeathMessage("");
                }
                return;
            }
            //if there is an affix for the player
            if (pde.getEntity().hasPermission(DEATH_MESSAGE_AFFIXES)) {
                //grab format from config file
                String format = ChatColor.translateAlternateColorCodes('&', ConfigManager.getInstance().getConfig().getString(ConfigNodes.DEATH_MESSAGES_FORMAT));

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
}
