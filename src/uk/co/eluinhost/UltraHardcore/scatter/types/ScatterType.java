package uk.co.eluinhost.UltraHardcore.scatter.types;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import uk.co.eluinhost.UltraHardcore.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.UltraHardcore.exceptions.WorldNotFoundException;
import uk.co.eluinhost.UltraHardcore.scatter.ScatterParams;

public abstract class ScatterType {

	public abstract String getScatterName();
	public abstract String getDescription();
	public abstract List<Location> getScatterLocations(ScatterParams params, int amount) throws WorldNotFoundException, MaxAttemptsReachedException;
	protected Random random = new Random();
	
	public static void setYHighest(Location loc){
		if(!loc.getChunk().isLoaded()){
			loc.getChunk().load(true);
		}	
		int y = loc.getWorld().getMaxHeight();
		for (; y >= 0; y--) {
			loc.setY(y);
			if(loc.getWorld().getBlockTypeIdAt(loc) != 0) {
				return;
			}
		}
		loc.getChunk().unload(false, true);
    }
}
