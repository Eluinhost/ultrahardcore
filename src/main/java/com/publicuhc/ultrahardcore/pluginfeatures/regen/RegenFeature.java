package com.publicuhc.ultrahardcore.pluginfeatures.regen;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.Plugin;


/**
 * RegenHandler
 * Handles the regeneration of players and cancels if its from being near full hunger
 *
 * @author ghowden
 */
@Singleton
public class RegenFeature extends UHCFeature {

    private static final int FOOD_LEVEL = 18;
    private static final double PLAYER_DEAD_HEALTH = 0.0;
    private static final float EXHAUSTION_OFFSET = 3.0F;

    public static final String NO_HEALTH_REGEN = BASE_PERMISSION+ "disableRegen";

    /**
     * Disables natural regen when enabled, normal when disabled
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private RegenFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * Whenever an entity regains health
     * @param erhe the entityregainhealthevent
     */
    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent erhe) {
        if (isEnabled()) {
            //If its a player regen
            if (erhe.getEntityType() == EntityType.PLAYER) {
                //If the player is in a hardcore world
                //If its just standard health regen
                if (erhe.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                    Player p = (Player) erhe.getEntity();
                    if (p.hasPermission(NO_HEALTH_REGEN)) {
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
    public String getFeatureID() {
        return "DisableRegen";
    }

    @Override
    public String getDescription() {
        return "Cancels a player's passive health regeneration";
    }
}
