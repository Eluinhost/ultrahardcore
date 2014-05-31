/*
 * FallProtector.java
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class FallProtector implements Protector {

    private final Map<Player, Location> m_protectedPlayers = new WeakHashMap<Player, Location>();

    /**
     * Called when a player moves
     * @param event the PlayerMoveEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void playerMoveEvent(PlayerMoveEvent event) {
        if (m_protectedPlayers.containsKey(event.getPlayer())) {
            Player player = event.getPlayer();
            Location newLocation = event.getTo();
            Location location = m_protectedPlayers.get(player);

            //Stop if moved over 1 square
            if (distanceXZ(location, newLocation) >= 1.0 || location.getY() < newLocation.getY()) {
                m_protectedPlayers.remove(player);
                return;
            }
            newLocation.setY(location.getY());
            event.setTo(newLocation);
        }
    }

    /**
     * Gets the distance between to locations in the XZ plane
     * @param l1 the first location
     * @param l2 the second location
     * @return the distance
     */
    private static double distanceXZ(Location l1, Location l2) {
        return Math.abs(l1.getX() - l2.getX()) + Math.abs(l1.getZ() - l2.getZ());
    }

    /**
     * Called when a player takes damage
     * @param ede the EntityDamageEvent
     */
    @EventHandler
    public void onPlayerHurt(EntityDamageEvent ede) {
        if (ede.getEntity() instanceof Player) {
            if (m_protectedPlayers.containsKey(ede.getEntity())) {
                switch (ede.getCause()) {
                    //stop suffocation, fall and void damage
                    case SUFFOCATION:
                    case FALL:
                    case VOID:
                        ede.setCancelled(true);
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public void addPlayer(Player player, Location loc) {
        m_protectedPlayers.put(player, loc);
    }
}
