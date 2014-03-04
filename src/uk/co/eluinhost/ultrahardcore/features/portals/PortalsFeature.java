package uk.co.eluinhost.ultrahardcore.features.portals;

import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class PortalsFeature extends UHCFeature {

    public static final String FROM_NETHER_NODE = "from_nether.";
    public static final String TO_NETHER_NODE = "to_nether.";

    /**
     * Changes the radius portals will connect, normal when disabled
     */
    public PortalsFeature() {
        super("PortalRanges","Change the radius portals can spawn in");
    }

    /**
     * When a portal event happends
     * @param entityPortalEvent the portal event
     */
    @EventHandler
    public void onPortalEvent(PlayerPortalEvent entityPortalEvent) {
        //if we're enabled
        if (isEnabled()) {
            //get the config
            FileConfiguration config = ConfigManager.getInstance().getConfig();

            //create a travel agent for the portal
            TravelAgent ta = entityPortalEvent.getPortalTravelAgent();

            //if they're in the nether
            if (entityPortalEvent.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {

                //set data from the nether
                ta.setCanCreatePortal(config.getBoolean(getBaseConfig()+FROM_NETHER_NODE+"allowed"));
                ta.setCreationRadius(config.getInt(getBaseConfig()+FROM_NETHER_NODE+"creation_range"));
                ta.setSearchRadius(config.getInt(getBaseConfig()+FROM_NETHER_NODE+"search_range"));
            } else {

                //set the data to the nether
                ta.setCanCreatePortal(config.getBoolean(getBaseConfig()+TO_NETHER_NODE+"allowed"));
                ta.setCreationRadius(config.getInt(getBaseConfig()+TO_NETHER_NODE+"creation_range"));
                ta.setSearchRadius(config.getInt(getBaseConfig()+TO_NETHER_NODE+"search_range"));
            }
        }
    }
}
