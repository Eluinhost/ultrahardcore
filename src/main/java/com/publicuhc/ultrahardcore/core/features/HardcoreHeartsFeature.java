/*
 * HardcoreHeartsFeature.java
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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import org.bukkit.plugin.Plugin;

/**
 * HardcoreHeartsFeature
 * <p/>
 * Enabled: Shows hardcore hearts on login to the server
 * Disabled: Nothing
 * <p/>
 * Requires ProtocolLib
 */
@Singleton
public class HardcoreHeartsFeature extends UHCFeature
{

    private final ProtocolManager m_manager = ProtocolLibrary.getProtocolManager();
    private final PacketListener m_listner;

    /**
     * Shows hardcore hearts in non hardcore worlds when enabled, requires protcollib
     *
     * @param plugin the plugin
     */
    @Inject
    private HardcoreHeartsFeature(Plugin plugin)
    {
        m_listner = new HardcoreHeartsListener(plugin);
    }

    @Override
    public void enableCallback()
    {
        m_manager.addPacketListener(m_listner);
    }

    @Override
    public void disableCallback()
    {
        m_manager.removePacketListener(m_listner);
    }

    @Override
    public String getFeatureID()
    {
        return "HardcoreHearts";
    }

    @Override
    public String getDescription()
    {
        return "Shows the hardcore hearts instead";
    }

    private static class HardcoreHeartsListener extends PacketAdapter
    {

        /**
         * listens for login packets to edit
         *
         * @param bukkitPlugin the plugin
         */
        HardcoreHeartsListener(Plugin bukkitPlugin)
        {
            //listen for login packets on the normal priority
            super(bukkitPlugin, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN);
        }

        @Override
        public void onPacketSending(PacketEvent event)
        {
            //if its a login packet write the first boolean to true (hardcore flag)
            if(event.getPacketType().equals(PacketType.Play.Server.LOGIN)) {
                event.getPacket().getBooleans().write(0, true);
            }
        }
    }
}
