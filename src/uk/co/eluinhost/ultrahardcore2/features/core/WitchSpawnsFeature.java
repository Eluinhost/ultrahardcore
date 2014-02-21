package uk.co.eluinhost.ultrahardcore.features.core;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class WitchSpawnsFeature extends UHCFeature {


    public WitchSpawnsFeature(boolean enabled) {
        super("WitchSpawnsFeature", enabled);
        setDescription("Allows natural witch spawns");
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent ese) {
        //when enabled witches can spawn, when not enabled they are cancelled
        //TODO this is stupid, must do better
        if (!isEnabled() && ese.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && ese.getEntity().getType() == EntityType.WITCH) {
            ese.setCancelled(true);
        }
    }

    @Override
    public void enableFeature() {

    }

    @Override
    public void disableFeature() {

    }
}
