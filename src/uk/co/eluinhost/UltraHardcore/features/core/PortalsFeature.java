package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class PortalsFeature extends UHCFeature {

    public PortalsFeature(boolean enabled) {
        super("PortalRanges", enabled);
        setDescription("Change the radius portals can spawn in");
    }

    @EventHandler
    public void onPortalEvent(PlayerPortalEvent entityPortalEvent) {
        if (isEnabled()) {
            FileConfiguration c = ConfigHandler.getConfig(ConfigHandler.MAIN);
            TravelAgent ta = entityPortalEvent.getPortalTravelAgent();
            if (entityPortalEvent.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
                ta.setCanCreatePortal(c.getBoolean(ConfigNodes.PORTAL_RANGES_FROM_NETHER_ALLOWED));
                ta.setCreationRadius(c.getInt(ConfigNodes.PORTAL_RANGES_FROM_NETHER_CREATION));
                ta.setSearchRadius(c.getInt(ConfigNodes.PORTAL_RANGES_FROM_NETHER_SEARCH));
            } else {
                ta.setCanCreatePortal(c.getBoolean(ConfigNodes.PORTAL_RANGES_TO_NETHER_ALLOWED));
                ta.setCreationRadius(c.getInt(ConfigNodes.PORTAL_RANGES_TO_NETHER_CREATION));
                ta.setSearchRadius(c.getInt(ConfigNodes.PORTAL_RANGES_TO_NETHER_SEARCH));
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
