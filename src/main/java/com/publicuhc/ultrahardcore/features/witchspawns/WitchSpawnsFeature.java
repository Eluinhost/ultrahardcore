package com.publicuhc.ultrahardcore.features.witchspawns;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.features.UHCFeature;

@Singleton
public class WitchSpawnsFeature extends UHCFeature {

    /**
     * Feature allows witches to spawn when enabled, disables it when disabled
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    private WitchSpawnsFeature(Plugin plugin, ConfigManager configManager) {
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
