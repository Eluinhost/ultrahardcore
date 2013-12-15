package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class WitchSpawnsFeature extends UHCFeature {


    public WitchSpawnsFeature(boolean enabled) {
        super(enabled);
        setFeatureID("WitchSpawns");
        setDescription("Allows natural witch spawns");
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent ese){
        if(!isEnabled()){
            if(ese.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)){
                ese.setCancelled(true);
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
