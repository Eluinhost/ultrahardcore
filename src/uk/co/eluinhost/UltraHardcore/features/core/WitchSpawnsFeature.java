package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class WitchSpawnsFeature extends UHCFeature {


    public WitchSpawnsFeature(boolean enabled) {
        super("WitchSpawnsFeature",enabled);
        setDescription("Allows natural witch spawns");
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent ese){
        if(!isEnabled()){
            if(ese.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)&&ese.getEntity().getType().equals(EntityType.WITCH)){
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
