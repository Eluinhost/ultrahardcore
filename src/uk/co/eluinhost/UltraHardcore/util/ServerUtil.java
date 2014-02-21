package uk.co.eluinhost.ultrahardcore.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;

public class ServerUtil {

	public static void broadcastForPermission(String message,String perm){
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.hasPermission(perm)){
				p.sendMessage(message);
			}
		}
	}

    public static void sendPlayerToServer(Player p,String serverName){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        p.sendPluginMessage(UltraHardcore.getInstance(), "BungeeCord", b.toByteArray());
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

    /**
     * Get the highest non air block at a location using it's Y or MAX_HEIGHT
     * @param loc The location to use
     * @param max whether to use the Y value in the location or max world height
     * @return the Y value of the highest non air block
     */
    public static int getYHighest(Location loc,boolean max){
        Location l = loc.clone();
        if(max){
            l.setY(l.getWorld().getMaxHeight()-1);
        }
        return getYHighest(l);
    }

    /**
     * Returns the highest non air block below the Y coordinate given in the location
     * @param l The location to use
     * @return int, the Y value of the highest non air block or 0
     */
    public static int getYHighest(Location l){
        Location loc = l.clone();
        if(!loc.getChunk().isLoaded()){
            loc.getChunk().load(true);
        }
        int y = loc.getBlockY();
        for (; y >= 0; y--) {
            loc.setY(y);
            if(loc.getBlock().getType() != Material.AIR) {
                loc.getChunk().unload(false, true);
                return y;
            }
        }
        loc.getChunk().unload(false, true);
        return 0;
    }

    /**
     * Sets Y value for the location to the highest non air block
     * @param loc the location to modify
     */
    public static void setYHighest(Location loc){
        loc.setY(getYHighest(loc,true));
    }

}
