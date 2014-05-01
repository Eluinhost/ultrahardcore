/*
 * TimerRunnable.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.timer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public class TimerRunnable implements Runnable {

    public static final int TICKS_PER_SECOND = 20;
    public static final float DRAGON_HEALTH = 200.0F;
    public static final long SECONDS_PER_HOUR = 3600;
    public static final long SECONDS_PER_MINUTE = 60;
    public static final int Y_COORD = -200;
    public static final int Y_MULTIPLIER = 32;
    public static final int X_MULTIPLIER = 32;
    public static final int Z_MULTIPLIER = 32;
    public static final int INVISIBLE_FLAG = 0x20;
    public static final int MAX_STRING_LEGNTH = 64;
    private static final int ENTITY_ID = Short.MAX_VALUE - 375;

    private final ProtocolManager m_protocolManager = ProtocolLibrary.getProtocolManager();
    private final PacketContainer m_spawnPacket = m_protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
    private final PacketContainer m_destroyPacket = m_protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);



    private int m_ticksLeft = 0;
    private int m_jobID = -1;
    private final Plugin m_plugin;
    private final String m_message;

    /**
     * A new timer
     * @param ticks the ticks to run for
     * @param message the message to display
     * @param plugin the plugin we run under
     */
    public TimerRunnable(int ticks, String message, Plugin plugin){
        m_ticksLeft = ticks;
        m_plugin = plugin;
        m_message = message;
        m_destroyPacket.getIntegerArrays().write(0, new int[]{ENTITY_ID});
    }

    /**
     * Start the runnable
     */
    public void start(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(m_plugin, this, 0, TICKS_PER_SECOND);
    }

    /**
     * Stop the timer
     */
    public void stopTimer() {
        if (m_jobID != -1) {
            Bukkit.getScheduler().cancelTask(m_jobID);
            m_jobID = -1;
        }
        destroyTimer();
    }

    /**
     * Converts the ticks to human readable
     * @param ticks the  number of ticks
     * @return the human readable version
     */
    public static String ticksToString(long ticks) {
        int hours = (int) Math.floor(ticks / (double)(SECONDS_PER_HOUR * 2)); //half seconds in a hour
        ticks -= hours * SECONDS_PER_HOUR * 2;
        int minutes = (int) Math.floor(ticks / (double)(SECONDS_PER_MINUTE * 2));    //half seconds in a minute
        ticks -= minutes * SECONDS_PER_MINUTE * 2;
        int seconds = (int) Math.floor(ticks / (double) 2);

        String output = "";
        if (hours > 0) {
            output += hours + "h";
        }
        if (minutes > 0) {
            output += minutes + "m";
        }
        output += seconds + "s";

        return output;
    }

    /**
     * @return Is it still running or not
     */
    public boolean isRunning(){
        return m_ticksLeft > 0;
    }

    /**
     * Gets rid of the timer for all players
     */
    private void destroyTimer() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                m_protocolManager.sendServerPacket(p, m_destroyPacket);
            }catch (InvocationTargetException ignored){}
        }
    }

    @Override
    public void run() {
        --m_ticksLeft;
        if (m_ticksLeft == 0) {
            stopTimer();
            return;
        }
        displayTextBar(m_message + ticksToString(m_ticksLeft), m_ticksLeft / (float) m_ticksLeft * DRAGON_HEALTH);
    }

    /**
     * Display the text for all
     * @param text the text to show
     * @param health the health for the bar to be on
     */
    private void displayTextBar(String text, float health) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                destroyTimer();
                PacketContainer pc = m_spawnPacket.deepClone();
                //noinspection deprecation
                pc.getIntegers()
                        .write(0, ENTITY_ID)
                        .write(1, (int) EntityType.ENDER_DRAGON.getTypeId())  //entity type ID
                        .write(2, (int) player.getLocation().getX() * X_MULTIPLIER)        //x
                        .write(3, Y_COORD * Y_MULTIPLIER)                                    //y
                        .write(4, (int) player.getLocation().getZ() * Z_MULTIPLIER);       //z
                WrappedDataWatcher watcher = pc.getDataWatcherModifier().read(0);
                watcher.setObject(0, (byte) INVISIBLE_FLAG);   //invisible
                watcher.setObject(6, health);   //health
                watcher.setObject(10, text.substring(0, Math.min(text.length(), MAX_STRING_LEGNTH)));
                m_protocolManager.sendServerPacket(player, pc);
            }catch (InvocationTargetException ignored){}
        }
    }
}
