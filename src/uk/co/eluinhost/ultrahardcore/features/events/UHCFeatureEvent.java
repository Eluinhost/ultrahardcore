package uk.co.eluinhost.ultrahardcore.features.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class UHCFeatureEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final UHCFeature m_feature;
    private boolean m_allowed = true;

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

    public void setAllowed(boolean allowed){
        m_allowed = allowed;
    }

    public boolean isAllowed(){
        return m_allowed;
    }
}
