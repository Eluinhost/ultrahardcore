package com.publicuhc.ultrahardcore.pluginfeatures.hardcorehearts;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.plugin.Plugin;

@Singleton
public class HardcoreHeartsFeature extends UHCFeature {

    private final ProtocolManager m_manager = ProtocolLibrary.getProtocolManager();
    private final PacketListener m_listner;

    /**
     * Shows hardcore hearts in non hardcore worlds when enabled, requires protcollib
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private HardcoreHeartsFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
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
