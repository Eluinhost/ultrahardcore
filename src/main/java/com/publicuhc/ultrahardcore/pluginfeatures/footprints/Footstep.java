/*
 * Footstep.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.footprints;

import org.bukkit.Location;

import java.util.UUID;

public class Footstep {

    private final Location m_location;
    private int m_timeRemaining;
    private final UUID m_playerID;

    /**
     * Make a new footstep
     * @param loc the location to display it
     * @param timeToLast the amount of footstep ticks to last
     * @param playerID the player that left the footprint
     */
    public Footstep(Location loc, int timeToLast, UUID playerID) {
        m_playerID = playerID;
        m_location = loc;
        m_timeRemaining = timeToLast;
    }

    /**
     * @return amount of time left
     */
    public int getTimeRemaining() {
        return m_timeRemaining;
    }

    /**
     * lower the amount of time left for the footprint to remain
     */
    public void decrementTimeRemaining() {
        m_timeRemaining -= 1;
    }

    /**
     * @return the location the footprint displays at
     */
    public Location getLocation() {
        return m_location;
    }

    /**
     * @return the name of the player who left the footprint
     */
    public UUID getPlayerID() {
        return m_playerID;
    }
}
