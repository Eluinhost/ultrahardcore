package uk.co.eluinhost.ultrahardcore.features.portals;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

@Singleton
public class PortalsFeature extends UHCFeature {

    /**
     * Changes the radius portals will connect, normal when disabled
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    public PortalsFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
    }

    /**
     * When a portal event happends
     * @param entityPortalEvent the portal event
     */
    @EventHandler
    public void onPortalEvent(PlayerPortalEvent entityPortalEvent) {
        //if we're enabled
        if (isEnabled()) {
            FileConfiguration config = getConfigManager().getConfig();

            //create a travel agent for the portal
            TravelAgent ta = entityPortalEvent.getPortalTravelAgent();
            //if they're in the nether
            if (entityPortalEvent.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
                //set data from the nether
                ta.setCanCreatePortal(config.getBoolean(getBaseConfig()+"from_nether.allowed"));
                ta.setCreationRadius(config.getInt(getBaseConfig()+"from_nether.creation_radius"));
                ta.setSearchRadius(config.getInt(getBaseConfig()+"from_nether.search_radius"));
            } else {
                //set the data to the nether
                ta.setCanCreatePortal(config.getBoolean(getBaseConfig()+"to_nether.allowed"));
                ta.setCreationRadius(config.getInt(getBaseConfig()+"to_nether.creation_radius"));
                ta.setSearchRadius(config.getInt(getBaseConfig()+"to_nether.search_radius"));
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "PortalRanges";
    }

    @Override
    public String getDescription() {
        return "Change the radius portals can spawn in";
    }
}
