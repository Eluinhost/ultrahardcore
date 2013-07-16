package uk.co.eluinhost.UltraHardcore.features.core;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class HardcoreHearts extends UHCFeature{

	ProtocolManager pm = ProtocolLibrary.getProtocolManager();
	PacketAdapter pa = null;
	
	public HardcoreHearts(boolean enabled) {
		super(enabled);
		setFeatureID("HardcoreHearts");
		setDescription("Shows the hardcore hearts instead");
	}

	@Override
	public void enableFeature() {
		if(pa == null){
			pa = new PacketAdapter(UltraHardcore.getInstance(), ConnectionSide.SERVER_SIDE, 
	                  ListenerPriority.NORMAL, GamePhase.LOGIN, Packets.Server.LOGIN){
	        	  @Override
	              public void onPacketSending(PacketEvent event) {
	        		  switch(event.getPacketID()){
	        		  case Packets.Server.LOGIN:
	        			  event.getPacket().getBooleans().write(0, true);
	        			 //event.getPacket().getBytes().write(2, (byte) 0);
	        		  }
	        	  }
	          };
		}
		pm.addPacketListener(pa);
	}

	@Override
	public void disableFeature() {
		if(pa != null){
			pm.removePacketListener(pa);
		}
	}

}
