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
import uk.co.eluinhost.UltraHardcore.util.MathsHelper;

public class RandomCircularType extends ScatterType{

	private final static String NAME = "RandomCircle";
	private final static String DESCRIPTION = "Randomly distributes locations evenly over a circular area";
	
	@Override
	public List<Location> getScatterLocations(ScatterParams scatterParams, int amount) throws WorldNotFoundException, MaxAttemptsReachedException {
		World world = Bukkit.getWorld(scatterParams.getWorld());
		if(world==null){
			throw new WorldNotFoundException();
		}
		ArrayList<Location> locations = new ArrayList<Location>();
		for(int k = 0;k<amount;k++){
			Location finalTeleport = new Location(world,0,0,0);
			boolean valid = false;
	    	mainloop: for(int i = 0;i<ScatterManager.MAX_TRIES;i++){
	    		//get a random angle between 0 and 2PI
		    	double randomAngle = random.nextDouble()*Math.PI*2d;
		    	//get a random radius for uniform circular distribution
				double newradius = (scatterParams.getRadius() * Math.sqrt(random.nextDouble()));
				
				//Convert back to cartesian
				double xcoord = MathsHelper.getXFromRadians(newradius, randomAngle)+scatterParams.getX();
				double zcoord = MathsHelper.getZFromRadians(newradius, randomAngle)+scatterParams.getZ();
				
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
						if(p.getLocation().distanceSquared(finalTeleport)<scatterParams.getMinDistanceSquared()){
							//Bukkit.getLogger().severe("TOO CLOSE PLAYER");
							continue mainloop;
						}
					}catch(IllegalArgumentException ignored){}
				}
				for(Location loc : locations){
					if(loc.distanceSquared(finalTeleport)<scatterParams.getMinDistanceSquared()){
						Bukkit.getLogger().severe("TOO CLOSE EXISTING");
						continue mainloop;
					}
				}
				
				//if the block isnt allowed get a new coord
				if(!scatterParams.blockIDAllowed(finalTeleport.getBlock().getTypeId())){
					//Bukkit.getLogger().severe("Block "+finalTeleport.getBlock()+" not allowed");
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
	
	@Override
	public String getScatterName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

}
