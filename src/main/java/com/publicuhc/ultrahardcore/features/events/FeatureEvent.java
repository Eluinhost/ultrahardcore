/*
 * FeatureEvent.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.features.events;

import com.publicuhc.ultrahardcore.features.Feature;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FeatureEvent extends Event implements Cancellable{

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Feature m_feature;
    private boolean m_cancelled;

    /**
     * Generic feature event
     * @param feature feature event is running for
     */
    protected FeatureEvent(Feature feature){
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
    public Feature getFeature(){
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
