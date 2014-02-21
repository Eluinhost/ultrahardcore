package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import uk.co.eluinhost.ultrahardcore.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.exceptions.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterParams;
import uk.co.eluinhost.ultrahardcore.util.MathsHelper;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class EvenCircumferenceType extends ScatterType{

	private static final String NAME = "EvenCircle";
	private static final String DESCRIPTION = "Puts players at even distances distance from each other along the circumference";
	
	@Override
	public List<Location> getScatterLocations(ScatterParams params, int amount) 
			throws WorldNotFoundException, MaxAttemptsReachedException{
		//angular difference between players
		 double increment = (2d*Math.PI)/amount;
		
		 //List of locations to return
		 ArrayList<Location> locations = new ArrayList<Location>();
		 
		 //If the world isn't valid throw a fit
		 World w = Bukkit.getWorld(params.getWorld());
		 if(w==null){
			 throw new WorldNotFoundException();
		 }
		 
		 //for all the things
		 for(int i = 0;i<amount;i++){
			 
			 //get which angle we are doing
			 double angle = i*increment;
			 
			 //convert from radians
			 double x = MathsHelper.getXFromRadians(params.getRadius(), angle);
			 double z = MathsHelper.getZFromRadians(params.getRadius(), angle);

			 //get the location with offset
			 Location finalTeleport = new Location(w,0,255,0);
			 finalTeleport.setX(x+params.getX()+0.5d);
			 finalTeleport.setZ(z+params.getZ()+0.5d);
   		
			 ServerUtil.setYHighest(finalTeleport);

			 //store it
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
