package uk.co.eluinhost.ultrahardcore.scatter;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScatterProtector implements Listener {
	private static HashMap<String,Location> protectedPlayers = new HashMap<String,Location>();
	
	@EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {
		if(protectedPlayers.containsKey(event.getPlayer().getName())){
			Player p = event.getPlayer();
			Location new_location = event.getTo();
	        Location location = protectedPlayers.get(p.getName());
	 
	        //Stop if moved over 1 square
	        if (distanceXZ(location,new_location) >= 1.0) {
	        	 protectedPlayers.remove(p.getName());
	            return;
	        }
	        else if (location.getY() < new_location.getY()) {
	        	 protectedPlayers.remove(p.getName());
	        	 return;
	        }
	        new_location.setY(location.getY());
	        event.setTo(new_location);
		}
    }
	
	private double distanceXZ(Location l1, Location l2){
		return Math.abs(l1.getX() - l2.getX()) + Math.abs(l1.getZ() - l2.getZ());
	}
	
	@EventHandler
	public void onPlayerHurt(EntityDamageEvent ede){
		if(ede.getEntity() instanceof Player){
			if(protectedPlayers.containsKey(((Player)ede.getEntity()).getName())){
				switch(ede.getCause()){
					case SUFFOCATION:
					case FALL:
					case VOID:
						ede.setCancelled(true);
					default:
				}
			}
		}
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent pqe){
		protectedPlayers.remove(pqe.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent pke){
		protectedPlayers.remove(pke.getPlayer().getName());
	}

	public void add(String name, Location loc) {
		protectedPlayers.put(name, loc);
	}
}
