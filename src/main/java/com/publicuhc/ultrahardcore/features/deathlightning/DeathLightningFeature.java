package com.publicuhc.ultrahardcore.features.deathlightning;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.features.UHCFeature;


/**
 * DeathLightningFeature
 * <p/>
 * hits people with lightning on death
 *
 * @author ghowden
 */
@Singleton
public class DeathLightningFeature extends UHCFeature {

    public static final String DEATH_LIGHTNING = BASE_PERMISSION + "deathLightning";

    /**
     * Strikes lightning on a player death
     * @param configManager the config manager
     * @param plugin the plugin
     */
    @Inject
    private DeathLightningFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin,configManager);
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

    @Override
    public String getFeatureID() {
        return "DeathLightning";
    }

    @Override
    public String getDescription() {
        return "Fake lightning on a player's corpse";
    }
}
