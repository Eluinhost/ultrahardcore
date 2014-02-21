package uk.co.eluinhost.ultrahardcore.features.core;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * RegenHandler
 * Handles the regeneration of players and cancels if its from being near full hunger
 *
 * @author ghowden
 */
public class RegenFeature extends UHCFeature {

    private static final int FOOD_LEVEL = 18;
    private static final double PLAYER_DEAD_HEALTH = 0.0;
    private static final float EXHAUSTION_OFFSET = 3.0F;

    public RegenFeature(boolean enabled) {
        super("DisableRegen", enabled);
        setDescription("Cancels a player's passive health regeneration");
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent erhe) {
        if (isEnabled()) {
            //If its a player regen
            if (erhe.getEntityType() == EntityType.PLAYER) {
                //If the player is in a hardcore world
                //If its just standard health regen
                if (erhe.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                    Player p = (Player) erhe.getEntity();
                    if (p.hasPermission(PermissionNodes.NO_HEALTH_REGEN)) {
                        if (p.getFoodLevel() >= FOOD_LEVEL && p.getHealth() > PLAYER_DEAD_HEALTH && p.getHealth() < p.getMaxHealth()) {
                            p.setExhaustion(p.getExhaustion() - EXHAUSTION_OFFSET);
                        }
                        //Cancel the event to stop the regen
                        erhe.setCancelled(true);
                    }
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
