package com.publicuhc.features.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.publicuhc.features.IFeature;

public class FeatureEvent extends Event implements Cancellable{

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final IFeature m_feature;
    private boolean m_cancelled;

    /**
     * Generic feature event
     * @param feature feature event is running for
     */
    protected FeatureEvent(IFeature feature){
        m_feature = feature;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * @return all the handlers
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * @return the feature involved in this event
     */
    public IFeature getFeature(){
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
