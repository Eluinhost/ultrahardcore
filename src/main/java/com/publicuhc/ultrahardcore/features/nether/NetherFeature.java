package com.publicuhc.ultrahardcore.features.nether;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;


/**
 * NetherFeature
 *
 * @author ghowden
 */
@Singleton
public class NetherFeature extends UHCFeature {

    public static final String ALLOW_NETHER = BASE_PERMISSION+"nether.allow";

    /**
     * Stops travelling to the nether when enabled
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private NetherFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
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

    @Override
    public String getFeatureID() {
        return "NetherFeature";
    }

    @Override
    public String getDescription() {
        return "Disables the use of nether portals";
    }
}
