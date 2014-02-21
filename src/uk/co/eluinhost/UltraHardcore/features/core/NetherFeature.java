package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEvent;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;


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
        if (isEnabled()) {
            if (epe.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                epe.setCancelled(true);
            }
        }
    }

    @Override
    public void enableFeature() {
    }

    @Override
    public void disableFeature() {
    }
}
