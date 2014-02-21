package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;


/**
 * EnderpearlsFeature
 * <p/>
 * Handles the damage taken from throwing enderpearls
 *
 * @author ghowden
 */
public class EnderpearlsFeature extends UHCFeature {

    public EnderpearlsFeature(boolean enabled) {
        super("Enderpearls", enabled);
        setDescription("Enderpearls cause no teleport damage");
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent ede) {
        if (isEnabled()) {
            if (ede.getDamager().getType().equals(EntityType.ENDER_PEARL)) {
                if (((Player) ede.getEntity()).hasPermission(PermissionNodes.NO_ENDERPEARL_DAMAGE)) {
                    ede.setCancelled(true);
                }
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
