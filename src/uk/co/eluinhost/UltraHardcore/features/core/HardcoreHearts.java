package uk.co.eluinhost.UltraHardcore.features.core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class HardcoreHearts extends UHCFeature {

    private final ProtocolManager m_manager = ProtocolLibrary.getProtocolManager();
    private final PacketListener m_packetAdapter = new HardcoreHeartsListener();

    public HardcoreHearts(boolean enabled) {
        super("HardcoreHearts", enabled);
        setDescription("Shows the hardcore hearts instead");
    }

    @Override
    public void enableFeature() {
        m_manager.addPacketListener(m_packetAdapter);
    }

    @Override
    public void disableFeature() {
        m_manager.removePacketListener(m_packetAdapter);
    }

    private static class HardcoreHeartsListener extends PacketAdapter {

        HardcoreHeartsListener(){
            //listen for login packets on the normal priority
            super(UltraHardcore.getInstance(),ListenerPriority.NORMAL,PacketType.Play.Server.LOGIN);
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
