package com.publicuhc.ultrahardcore.features.enderpearls;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.features.UHCFeature;


/**
 * EnderpearlsFeature
 * Handles the damage taken from throwing enderpearls
 *
 * @author ghowden
 */
@Singleton
public class EnderpearlsFeature extends UHCFeature {

    public static final String NO_ENDERPEARL_DAMAGE = BASE_PERMISSION + "noEnderpearlDamage";

    /**
     * Enderpearls cause no damage
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    private EnderpearlsFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
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

    @Override
    public String getFeatureID() {
        return "Enderpearls";
    }

    @Override
    public String getDescription() {
        return "Enderpearls cause no teleport damage";
    }
}
