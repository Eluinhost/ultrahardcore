package uk.co.eluinhost.ultrahardcore.features.nether;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.permissions.Permissible;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * NetherFeature
 *
 * @author ghowden
 */
public class NetherFeature extends UHCFeature {

    public static final String ALLOW_NETHER = BASE_PERMISSION+"nether.allow";

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
        if (isEnabled() && epe.getEntity() instanceof Permissible) {
            //if they're going into the nether cancel it
            if(((Permissible) epe.getEntity()).hasPermission(ALLOW_NETHER)){
                return;
            }
            if (epe.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                epe.setCancelled(true);
            }
        }
    }
}
