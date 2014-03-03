package uk.co.eluinhost.ultrahardcore.features.nether;

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

    /**
     * Stops travelling to the nether when enabled
     */
    public NetherFeature() {
        super("NetherFeature","Disables the use of nether portals");
    }

    /**
     * On portal events
     * @param epe related event
     */
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
