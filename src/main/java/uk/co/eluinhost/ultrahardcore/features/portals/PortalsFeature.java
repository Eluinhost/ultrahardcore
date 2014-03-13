package uk.co.eluinhost.ultrahardcore.features.portals;

import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class PortalsFeature extends UHCFeature {

    private final boolean m_fromAllowed;
    private final int m_fromCreation;
    private final int m_fromSearch;

    private final boolean m_toAllowed;
    private final int m_toCreation;
    private final int m_toSearch;

    /**
     * Changes the radius portals will connect, normal when disabled
     */
    public PortalsFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, "PortalRanges","Change the radius portals can spawn in", configManager);
        FileConfiguration config = configManager.getConfig();
        m_fromAllowed = config.getBoolean(getBaseConfig()+"from_nether.allowed");
        m_fromSearch = config.getInt(getBaseConfig()+"from_nether.search_radius");
        m_fromCreation = config.getInt(getBaseConfig()+"from_nether.creation_radius");
        m_toAllowed = config.getBoolean(getBaseConfig()+"to_nether.allowed");
        m_toSearch = config.getInt(getBaseConfig()+"to_nether.search_radius");
        m_toCreation = config.getInt(getBaseConfig()+"to_nether.creation_radius");
    }

    /**
     * When a portal event happends
     * @param entityPortalEvent the portal event
     */
    @EventHandler
    public void onPortalEvent(PlayerPortalEvent entityPortalEvent) {
        //if we're enabled
        if (isEnabled()) {
            //create a travel agent for the portal
            TravelAgent ta = entityPortalEvent.getPortalTravelAgent();
            //if they're in the nether
            if (entityPortalEvent.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
                //set data from the nether
                ta.setCanCreatePortal(m_fromAllowed);
                ta.setCreationRadius(m_fromCreation);
                ta.setSearchRadius(m_fromSearch);
            } else {
                //set the data to the nether
                ta.setCanCreatePortal(m_toAllowed);
                ta.setCreationRadius(m_toCreation);
                ta.setSearchRadius(m_toSearch);
            }
        }
    }
}
