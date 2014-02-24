package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.services.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterParams;
import uk.co.eluinhost.ultrahardcore.util.MathsHelper;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class RandomCircularType extends AbstractScatterType {

    private static final String NAME = "RandomCircle";
    private static final String DESCRIPTION = "Randomly distributes locations evenly over a circular area";
    public static final double X_OFFSET = 0.5d;
    public static final double Z_OFFSET = 0.5d;

    public RandomCircularType(){
        super(NAME,DESCRIPTION);
    }

    @Override
    public List<Location> getScatterLocations(ScatterParams params, int amount) throws WorldNotFoundException, MaxAttemptsReachedException {
        World world = Bukkit.getWorld(params.getWorld());
        if (world == null) {
            throw new WorldNotFoundException();
        }
        AbstractList<Location> locations = new ArrayList<Location>();
        for (int k = 0; k < amount; k++) {
            Location finalTeleport = new Location(world, 0, 0, 0);
            boolean valid = false;
            for (int i = 0; i < ScatterManager.MAX_TRIES; i++) {
                //get a random angle between 0 and 2PI
                double randomAngle = getRandom().nextDouble() * Math.PI * 2d;
                //get a random radius for uniform circular distribution
                double newradius = params.getRadius() * Math.sqrt(getRandom().nextDouble());

                //Convert back to cartesian
                double xcoord = MathsHelper.getXFromRadians(newradius, randomAngle) + params.getCenterX();
                double zcoord = MathsHelper.getZFromRadians(newradius, randomAngle) + params.getCenterZ();

                //get the center of the block/s
                xcoord = Math.round(xcoord) + X_OFFSET;
                zcoord = Math.round(zcoord) + Z_OFFSET;

                //set the locations coordinates
                finalTeleport.setX(xcoord);
                finalTeleport.setZ(zcoord);

                //get the highest block in the Y coordinate
                ServerUtil.setYHighest(finalTeleport);

                if (isLocationTooClose(finalTeleport, locations, params.getMinDistance())) {
                    continue;
                }

                //if the block isnt allowed get a new coord
                if (!params.blockAllowed(finalTeleport.getBlock().getType())) {
                    continue;
                }

                //valid teleport, exit
                valid = true;
                break;
            }
            if (!valid) {
                throw new MaxAttemptsReachedException();
            }
            locations.add(finalTeleport);
        }
        return locations;
    }
}
