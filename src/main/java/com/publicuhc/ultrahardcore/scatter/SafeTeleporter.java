/*
 * SafeTeleporter.java
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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SafeTeleporter extends Teleporter {

    private final ScatterManager m_scatterManager;

    /**
     * @param player   the player to teleport
     * @param loc      the location to teleport to (2 is added to the Y coordinate)
     * @param manager the scatter manager
     */
    public SafeTeleporter(Player player, Location loc, ScatterManager manager) {
        super(player, loc);
        m_scatterManager = manager;
    }

    @Override
    public boolean teleport() {
        Player player = getPlayer();
        if(player == null){
            return false;
        }
        Location location = getLocation();
        m_scatterManager.teleportSafe(player,location);
        player.sendMessage(ChatColor.GOLD + "You were teleported "
                + (getTeamName() == null ? "solo" : "with team " + getTeamName())
                + " to " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
        return true;
    }
}
