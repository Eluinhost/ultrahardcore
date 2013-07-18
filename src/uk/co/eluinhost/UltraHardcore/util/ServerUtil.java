package uk.co.eluinhost.UltraHardcore.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class ServerUtil {

	public static void broadcastForPermission(String message,String perm){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.hasPermission(perm)){
				p.sendMessage(message);
			}
		}
	}
	
	public static List<String> getOnlinePlayers(){
		ArrayList<String> p = new ArrayList<String>();
		for(Player pl : Bukkit.getOnlinePlayers()){
			p.add(pl.getName());
		}
		return p;
	}
	
	public static List<String> getWorldNames(){
		ArrayList<String> p = new ArrayList<String>();
		for(World w : Bukkit.getWorlds()){
			p.add(w.getName());
		}
		return p;
	}
	
	public static List<String> getWorldNamesWithSpawn(){
		ArrayList<String> p = new ArrayList<String>();
		for(World w : Bukkit.getWorlds()){
			Location l = w.getSpawnLocation();
			p.add(w.getName()+":"+l.getBlockX()+","+l.getBlockZ());
		}
		return p;
	}



    public static BlockFace getCardinalDirection(Player player) {
        double yaw = (player.getLocation().getYaw());
        yaw = Math.toRadians(yaw);
        return BlockFace2DVector.getClosest(yaw);
    }

}
