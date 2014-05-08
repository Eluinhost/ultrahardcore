/*
 * FootprintFeature.java
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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Singleton
public class FootprintFeature extends UHCFeature implements Runnable {

    public static final float REGULAR_OFFSET = 1.05F;
    public static final float SNOW_OFFSET = 1.13F;

    public static final int REPEAT_INTERVAL = 40;

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    private final PacketContainer m_defaultPacket = PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.WORLD_PARTICLES);

    public static final String FOOTPRINTS_FOR_PLAYER = BASE_PERMISSION + "footprints.leavePrints";

    private final Collection<Footstep> m_footsteps = new ArrayList<Footstep>();
    private int m_jobID = -1;

    /**
     * Leave 'footprints' behind you, requires protocollib
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private FootprintFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    @Override
    protected void enableCallback() {
        m_jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), this, 1, REPEAT_INTERVAL);
    }

    @Override
    protected void disableCallback() {
        Bukkit.getScheduler().cancelTask(m_jobID);
        m_jobID = -1;
        m_footsteps.clear();
    }

    @Override
    public void run() {
        FileConfiguration config = getConfigManager().getConfig("main");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(FOOTPRINTS_FOR_PLAYER) || p.getGameMode() == GameMode.CREATIVE) {
                continue;
            }
            Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block.getType() == Material.AIR) {
                block = block.getRelative(BlockFace.DOWN);
            }
            Location loc = p.getLocation();
            int minRender = config.getInt(getBaseConfig()+"mindistance");
            int minRenderSquared = minRender * minRender;

            if (block.getType() != Material.AIR && block.getType() != Material.WATER && isClearOfFootprints(loc, minRenderSquared, p.getUniqueId())) {
                float offset = REGULAR_OFFSET;
                if (block.getType() == Material.SNOW) {
                    offset = SNOW_OFFSET;
                }
                loc.setY(block.getLocation().getY() + offset);
                Footstep newFootstep = new Footstep(
                    loc,
                    config.getInt(getBaseConfig()+"time") / 2,
                    p.getUniqueId()
                );
                sendFootstep(newFootstep);
                m_footsteps.add(newFootstep);
            }
        }
        Iterator<Footstep> iterator = m_footsteps.iterator();
        while (iterator.hasNext()) {
            Footstep footstep = iterator.next();
            footstep.decrementTimeRemaining();
            if (footstep.getTimeRemaining() <= 0) {
                iterator.remove();
            } else {
                sendFootstep(footstep);
            }
        }
    }

    /**
     * Checks for footprints within the distance of the location for the player
     * @param loc Location
     * @param distance int
     * @param playerID the player ID
     * @return boolean true if clear, false if not
     */
    private boolean isClearOfFootprints(Location loc, int distance, UUID playerID) {
        for (Footstep footstep : m_footsteps) {
            if (footstep.getPlayerID().equals(playerID)) {
                try {
                    if (footstep.getLocation().distanceSquared(loc) < distance) {
                        return false;
                    }
                } catch (IllegalArgumentException ignored) {/*happens on different dimension*/}
            }
        }
        return true;
    }

    /**
     * Sends the footstep to nearby players
     * @param footstep the footstep to send
     */
    private void sendFootstep(Footstep footstep){
        m_defaultPacket.getStrings().write(0, "footstep");
        Location loc = footstep.getLocation();
        m_defaultPacket.getFloat()
                .write(0, (float) loc.getX())//x
                .write(1, (float) loc.getY())//y
                .write(2, (float) loc.getZ())
                .write(3, 0.0F)//offsetx
                .write(4, 0.0F)//offsety
                .write(5, 0.0F)//offsetz
                .write(6, 1.0F);
        m_defaultPacket.getIntegers().write(0, 1);
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                int maxRender = getConfigManager().getConfig("main").getInt(getBaseConfig()+"maxdistance");
                if (p.getLocation().distanceSquared(loc) < maxRender * maxRender) {
                    PROTOCOL_MANAGER.sendServerPacket(p, m_defaultPacket);
                }
            } catch (IllegalArgumentException ignored) {
                //wrong world, doesn't matter
            } catch (InvocationTargetException ignored) {}
        }
    }

    @Override
    public String getFeatureID() {
        return "Footprints";
    }

    @Override
    public String getDescription() {
        return "Leave footprints behind you...";
    }

    @Override
    public List<String> getStatus() {
        List<String> status = new ArrayList<String>();
        status.add(ChatColor.GRAY + "--- Time to last: " + getConfigManager().getConfig("main").getInt(getBaseConfig()+"time"));
        return status;
    }
}
