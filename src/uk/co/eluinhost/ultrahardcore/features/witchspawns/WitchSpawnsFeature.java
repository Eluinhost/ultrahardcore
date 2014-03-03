package uk.co.eluinhost.ultrahardcore.features.witchspawns;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class WitchSpawnsFeature extends UHCFeature {

    /**
     * Feature allows witches to spawn when enabled, disables it when disabled
     */
    public WitchSpawnsFeature() {
        super("WitchSpawnsFeature","Allows natural witch spawns");
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
}
