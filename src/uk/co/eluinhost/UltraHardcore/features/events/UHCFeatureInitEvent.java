package uk.co.eluinhost.UltraHardcore.features.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class UHCFeatureInitEvent extends Event{

    private static final HandlerList handlers = new HandlerList();
    private final UHCFeature feature;
    private boolean allowed = true;

    public UHCFeatureInitEvent(UHCFeature feature){
        this.feature = feature;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UHCFeature getFeature(){
        return feature;
    }

    public void setAllowed(boolean allowed){
        this.allowed = allowed;
    }

    public boolean isAllowed(){
        return allowed;
    }

}
