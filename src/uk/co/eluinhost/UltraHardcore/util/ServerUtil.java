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

    private static BlockFace[] block_faces = new BlockFace[]{
            BlockFace.NORTH,
            BlockFace.NORTH_NORTH_EAST,
            BlockFace.NORTH_EAST,
            BlockFace.EAST_NORTH_EAST,
            BlockFace.EAST,
            BlockFace.EAST_SOUTH_EAST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_SOUTH_EAST,
            BlockFace.SOUTH,
            BlockFace.SOUTH_SOUTH_WEST,
            BlockFace.SOUTH_WEST,
            BlockFace.WEST_SOUTH_WEST,
            BlockFace.WEST,
            BlockFace.WEST_NORTH_WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH_NORTH_WEST};

    public static BlockFace getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        return block_faces[(int) (rotation / 22.5)];
    }

}
