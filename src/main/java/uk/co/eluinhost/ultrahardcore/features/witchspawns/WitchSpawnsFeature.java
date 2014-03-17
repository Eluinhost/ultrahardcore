package uk.co.eluinhost.ultrahardcore.features.witchspawns;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

@Singleton
public class WitchSpawnsFeature extends UHCFeature {

    /**
     * Feature allows witches to spawn when enabled, disables it when disabled
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    public WitchSpawnsFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
    }

    /**
     * Whenever a creature spawns
     * @param ese the creaturespawnevent
     */
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent ese) {
        //when enabled witches can spawn, when not enabled they are cancelled
        if (!isEnabled() && ese.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && ese.getEntity().getType() == EntityType.WITCH) {
            ese.setCancelled(true);
        }
    }

    @Override
    public String getFeatureID() {
        return "WitchSpawns";
    }

    @Override
    public String getDescription() {
        return "Allows natural witch spawns";
    }
}
