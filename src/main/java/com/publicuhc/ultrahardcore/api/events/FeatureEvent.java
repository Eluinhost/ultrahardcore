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

package com.publicuhc.ultrahardcore.api.events;

import com.publicuhc.ultrahardcore.api.Feature;
import org.bukkit.event.Cancellable;

public abstract class FeatureEvent extends UHCEvent implements Cancellable
{

    private final Feature feature;
    private boolean cancelled;

    /**
     * Generic feature event
     *
     * @param feature feature event is running for
     */
    protected FeatureEvent(Feature feature)
    {
        this.feature = feature;
    }

    /**
     * @return the feature involved in this event
     */
    public Feature getFeature()
    {
        return feature;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }
}
