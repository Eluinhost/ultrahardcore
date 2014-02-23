package uk.co.eluinhost.ultrahardcore.features.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class UHCFeatureEvent extends Event implements Cancellable{

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final UHCFeature m_feature;
    private boolean m_cancelled = true;

    protected UHCFeatureEvent(UHCFeature feature){
        m_feature = feature;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public UHCFeature getFeature(){
        return m_feature;
    }

    @Override
    public boolean isCancelled() {
        return m_cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        m_cancelled = cancelled;
    }
}
