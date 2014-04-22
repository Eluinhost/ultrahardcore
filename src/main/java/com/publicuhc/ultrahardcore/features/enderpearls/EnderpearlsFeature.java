package com.publicuhc.ultrahardcore.features.enderpearls;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;


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
     * @param translate the translator
     */
    @Inject
    private EnderpearlsFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
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
