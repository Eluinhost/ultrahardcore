/*
 * HardcoreHeartsFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.hardcorehearts;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.plugin.Plugin;

/**
 * HardcoreHeartsFeature
 *
 * Enabled: Shows hardcore hearts on login to the server
 * Disabled: Nothing
 *
 * Requires ProtocolLib
 */
@Singleton
public class HardcoreHeartsFeature extends UHCFeature {

    private final ProtocolManager m_manager = ProtocolLibrary.getProtocolManager();
    private final PacketListener m_listner;

    /**
     * Shows hardcore hearts in non hardcore worlds when enabled, requires protcollib
     *
     * @param plugin the plugin
     */
    @Inject
    private HardcoreHeartsFeature(Plugin plugin) {
        m_listner = new HardcoreHeartsListener(plugin);
    }

    @Override
    public void enableCallback(){
        m_manager.addPacketListener(m_listner);
    }

    @Override
    public void disableCallback(){
        m_manager.removePacketListener(m_listner);
    }

    @Override
    public String getFeatureID() {
        return "HardcoreHearts";
    }

    @Override
    public String getDescription() {
        return "Shows the hardcore hearts instead";
    }

    private static class HardcoreHeartsListener extends PacketAdapter {

        /**
         * listens for login packets to edit
         * @param bukkitPlugin the plugin
         */
        HardcoreHeartsListener(Plugin bukkitPlugin){
            //listen for login packets on the normal priority
            super(bukkitPlugin,ListenerPriority.NORMAL,PacketType.Play.Server.LOGIN);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            //if its a login packet write the first boolean to true (hardcore flag)
            if (event.getPacketType().equals(PacketType.Play.Server.LOGIN)) {
                event.getPacket().getBooleans().write(0, true);
            }
        }
    }
}
