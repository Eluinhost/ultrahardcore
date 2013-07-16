package uk.co.eluinhost.UltraHardcore.scatter.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import uk.co.eluinhost.UltraHardcore.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.UltraHardcore.exceptions.WorldNotFoundException;
import uk.co.eluinhost.UltraHardcore.scatter.ScatterManager;
import uk.co.eluinhost.UltraHardcore.scatter.ScatterParams;

public class RandomSquareType extends ScatterType{

	private static final String NAME = "RandomSquare";
	private static final String DESCRIPTION = "Uniformly scatter over a sqaure with sides length radius*2";

	@Override
	public String getScatterName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public List<Location> getScatterLocations(ScatterParams params, int amount)
			throws WorldNotFoundException, MaxAttemptsReachedException {
		World world = Bukkit.getWorld(params.getWorld());
		if(world==null){
			throw new WorldNotFoundException();
		}
		ArrayList<Location> locations = new ArrayList<Location>();
		for(int k = 0;k<amount;k++){
			Location finalTeleport = new Location(world,0,0,0);
			boolean valid = false;
	    	mainloop: for(int i = 0;i<ScatterManager.MAX_TRIES;i++){
	    		//get a coords
		    	double xcoord = random.nextDouble()*params.getRadius()*2;
		    	double zcoord = random.nextDouble()*params.getRadius()*2;
		    	xcoord -= params.getRadius();
		    	zcoord -= params.getRadius();
		    	xcoord += params.getX();
				zcoord += params.getZ();
				
				//get the center of the block/s
				xcoord = Math.round(xcoord) + 0.5d;
				zcoord = Math.round(zcoord) + 0.5d;
				
				//set the locations coordinates
				finalTeleport.setX(xcoord);
				finalTeleport.setZ(zcoord);
				
				//get the highest block in the Y coordinate
				setYHighest(finalTeleport);
				
				//If the coordinate is too close to a player get a new coord
				for(Player p : Bukkit.getOnlinePlayers()){
					try{
						if(p.getLocation().distanceSquared(finalTeleport)<params.getMinDistanceSquared()){
							continue mainloop;
						}
					}catch(IllegalArgumentException ignored){}
				}
				for(Location loc : locations){
					if(loc.distanceSquared(finalTeleport)<params.getMinDistanceSquared()){
						continue mainloop;
					}
				}
				
				//if the block isnt allowed get a new coord
				if(!params.blockIDAllowed(finalTeleport.getWorld().getBlockTypeIdAt(finalTeleport))){
					continue;
				}
				
				//valid teleport, exit
				valid = true;
				break;
	    	}
	    	if(!valid){
	    		throw new MaxAttemptsReachedException();
	    	}
		   	locations.add(finalTeleport);
		}
		return locations;
	}

}
