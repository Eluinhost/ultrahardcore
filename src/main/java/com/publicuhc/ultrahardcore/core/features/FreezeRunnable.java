/*
 * FreezeRunnable.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.ultrahardcore.core.features;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FreezeRunnable extends BukkitRunnable implements Listener
{

    private final List<PotionEffect> m_effects;
    private final Set<UUID> m_playerSet = new HashSet<UUID>();

    public FreezeRunnable(List<PotionEffect> effects)
    {
        m_effects = effects;
    }

    public void addPlayer(Player player)
    {
        addPlayer(player.getUniqueId());
    }

    public void addPlayer(UUID player)
    {
        m_playerSet.add(player);
    }

    public void removePlayer(Player player)
    {
        removePlayer(player.getUniqueId());
        removePotionEffects(player);
    }

    private void removePotionEffects(Player player)
    {
        for(PotionEffect effect : m_effects) {
            if(player.hasPotionEffect(effect.getType())) {
                player.removePotionEffect(effect.getType());
            }
        }
    }

    public void removePlayer(UUID player)
    {
        m_playerSet.remove(player);
    }

    public boolean isPlayerFrozen(Player player)
    {
        return isPlayerFrozen(player.getUniqueId());
    }

    public boolean isPlayerFrozen(UUID uuid)
    {
        return m_playerSet.contains(uuid);
    }

    public void clear()
    {
        for(UUID uuid : m_playerSet) {
            Player player = Bukkit.getPlayer(uuid);
            if(null != player) {
                removePotionEffects(player);
            }
        }
        m_playerSet.clear();
    }

    public void addPlayers(Player... players)
    {
        for(Player player : players) {
            addPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveEvent(PlayerMoveEvent pme)
    {
        //check if we're running
        try {
            getTaskId();
        } catch(IllegalStateException ignored) {
            return;
        }

        Player player = pme.getPlayer();
        if(isPlayerFrozen(player)) {
            Location newLocation = pme.getFrom();
            newLocation.setYaw(pme.getTo().getYaw());
            newLocation.setPitch(pme.getTo().getPitch());
            newLocation.setY(pme.getTo().getY());
            pme.setTo(newLocation);
        }
    }

    @Override
    public void run()
    {
        for(UUID entry : m_playerSet) {
            Player player = Bukkit.getPlayer(entry);
            if(null == player) {
                continue;
            }

            for(PotionEffect effect : m_effects) {
                player.addPotionEffect(effect, true);
            }
        }
    }
}
