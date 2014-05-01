/*
 * Teleporter.java
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

package com.publicuhc.ultrahardcore.scatter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;

public abstract class Teleporter {

    private final WeakReference<Player> m_player;
    private Location m_location;
    private String m_team;

    private static final Vector Y_OFFSET = new Vector(0,2,0);

    /**
     * @param player the player to teleport
     * @param loc the location to teleport to (2 is added to the Y coordinate)
     */
    protected Teleporter(Player player, Location loc) {
        m_player = new WeakReference<Player>(player);
        m_location = loc.add(Y_OFFSET);
    }

    /**
     * Get the player to be teleported, null if player was GCd
     * @return Player
     */
    public Player getPlayer() {
        return m_player.get();
    }

    /**
     * Get the name of the team to teleport as
     * @return String
     */
    public String getTeamName() {
        return m_team;
    }

    /**
     * Try to process this teleport
     * @return true if teleport went through, false if player object wasn't found
     */
    public abstract boolean teleport();

    /**
     * @param team the team name to scatter with
     */
    public void setTeam(String team) {
        m_team = team;
    }

    /**
     * @return the team name to scatter with
     */
    public String getTeam(){
        return m_team;
    }

    /**
     * @param location the location to teleport to, 2 is added to Y
     */
    public void setLocation(Location location) {
        m_location = location.add(Y_OFFSET);
    }

    /**
     * @return the location to teleport to
     */
    public Location getLocation(){
        return m_location;
    }
}
