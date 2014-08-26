/*
 * AddonInitializeEvent.java
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

import com.publicuhc.ultrahardcore.api.UHCAddonModule;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class AddonInitializeEvent extends UHCEvent implements Cancellable
{
    private final UHCAddonModule addon;
    private boolean cancelled = false;

    /**
     * Called whenever an addon is registed before initialization. Can be used to add custom bindings to all addons?
     *
     * @param module the module being loaded
     */
    public AddonInitializeEvent(UHCAddonModule module)
    {
        addon = module;
    }

    public UHCAddonModule getAddon()
    {
        return addon;
    }

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers()
    {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLER_LIST;
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
