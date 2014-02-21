package uk.co.eluinhost.ultrahardcore.features.core;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEvent;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * NetherFeature
 *
 * @author ghowden
 */
public class NetherFeature extends UHCFeature {

    public NetherFeature(boolean enabled) {
        super("NetherFeature", enabled);
        setDescription("Disables the use of nether portals");
    }

    @EventHandler
    public void onPortalEvent(EntityPortalEvent epe) {
        //if it's enabled
        if (isEnabled()) {
            //if they're going into the nether cancel it
            //TODO add a permission for this
            if (epe.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                epe.setCancelled(true);
            }
        }
    }
}
