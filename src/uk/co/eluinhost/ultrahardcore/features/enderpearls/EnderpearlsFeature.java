package uk.co.eluinhost.ultrahardcore.features.enderpearls;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.permissions.Permissible;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * EnderpearlsFeature
 * Handles the damage taken from throwing enderpearls
 *
 * @author ghowden
 */
public class EnderpearlsFeature extends UHCFeature {

    public static final String NO_ENDERPEARL_DAMAGE = BASE_PERMISSION + "noEnderpearlDamage";

    /**
     * Enderpearls cause no damage
     */
    public EnderpearlsFeature() {
        super("Enderpearls","Enderpearls cause no teleport damage");
    }

    /**
     * Whenever an entity is hurt
     * @param ede the damage event
     */
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent ede) {
        if (isEnabled()) {
            if (ede.getDamager().getType() == EntityType.ENDER_PEARL) {
                if (((Permissible) ede.getEntity()).hasPermission(NO_ENDERPEARL_DAMAGE)) {
                    ede.setCancelled(true);
                }
            }
        }
    }
}
