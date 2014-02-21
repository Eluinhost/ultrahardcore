package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.exceptions.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.scatter.PlayerTeleportMapping;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterParams;

public abstract class ScatterType {

	public abstract String getScatterName();
	public abstract String getDescription();
	public abstract List<Location> getScatterLocations(ScatterParams params, int amount) throws WorldNotFoundException, MaxAttemptsReachedException;
	protected Random random = new Random();

    protected boolean isLocationToClose(Location loc, ArrayList<Location> existing, Double distance){
        Double d_sqaured = distance*distance;
        for(Player p : Bukkit.getOnlinePlayers()){
            try{
                if(p.getLocation().distanceSquared(loc)<d_sqaured){
                    return true;
                }
            }catch(IllegalArgumentException ignored){}
        }
        for(Location l : existing){
            if(l.distanceSquared(loc)<d_sqaured){
                return true;
            }
        }
        for(PlayerTeleportMapping ptm : ScatterManager.getRemainingTeleports()){
            if(ptm.getLocation().distanceSquared(loc)<d_sqaured){
                return true;
            }
        }
        return false;
    }
}
