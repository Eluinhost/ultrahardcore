package uk.co.eluinhost.ultrahardcore.features.core;

import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class PortalsFeature extends UHCFeature {

    public PortalsFeature() {
        super("PortalRanges");
        setDescription("Change the radius portals can spawn in");
    }

    @EventHandler
    public void onPortalEvent(PlayerPortalEvent entityPortalEvent) {
        //if we're enabled
        if (isEnabled()) {
            //get the config
            FileConfiguration config = ConfigHandler.getConfig(ConfigHandler.MAIN);

            //create a travel agent for the portal
            TravelAgent ta = entityPortalEvent.getPortalTravelAgent();

            //if they're in the nether
            if (entityPortalEvent.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {

                //set data from the nether
                ta.setCanCreatePortal(config.getBoolean(ConfigNodes.PORTAL_RANGES_FROM_NETHER_ALLOWED));
                ta.setCreationRadius(config.getInt(ConfigNodes.PORTAL_RANGES_FROM_NETHER_CREATION));
                ta.setSearchRadius(config.getInt(ConfigNodes.PORTAL_RANGES_FROM_NETHER_SEARCH));
            } else {

                //set the data to the nether
                ta.setCanCreatePortal(config.getBoolean(ConfigNodes.PORTAL_RANGES_TO_NETHER_ALLOWED));
                ta.setCreationRadius(config.getInt(ConfigNodes.PORTAL_RANGES_TO_NETHER_CREATION));
                ta.setSearchRadius(config.getInt(ConfigNodes.PORTAL_RANGES_TO_NETHER_SEARCH));
            }
        }
    }
}
