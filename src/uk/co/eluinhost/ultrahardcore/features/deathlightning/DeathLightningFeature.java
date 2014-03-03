package uk.co.eluinhost.ultrahardcore.features.deathlightning;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * DeathLightningFeature
 * <p/>
 * hits people with lightning on death
 *
 * @author ghowden
 */
public class DeathLightningFeature extends UHCFeature {

    public static final String DEATH_LIGHTNING = BASE_PERMISSION + "deathLightning";

    /**
     * Strikes lightning on a player death
     */
    public DeathLightningFeature() {
        super("DeathLightning","Fake lightning on a player's corpse");
    }

    /**
     * Whenever a player dies
     * @param pde the death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (isEnabled()) {
            if (pde.getEntity().hasPermission(DEATH_LIGHTNING)) {
                pde.getEntity().getWorld().strikeLightningEffect(pde.getEntity().getLocation());
            }
        }
    }
}
