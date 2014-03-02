package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.scatter.Parameters;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.util.MathsHelper;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class RandomCircularType extends AbstractScatterType {

    private static final String SCATTER_NAME = "RandomCircle";
    private static final String DESCRIPTION = "Randomly distributes locations evenly over a circular area";

    /**
     * Scatters within a circle randomly
     */
    public RandomCircularType(){
        super(SCATTER_NAME,DESCRIPTION);
    }

    @Override
    public List<Location> getScatterLocations(Parameters params, int amount) throws MaxAttemptsReachedException {
        Location center = params.getScatterLocation();
        List<Location> locations = new ArrayList<Location>();
        for (int k = 0; k < amount; k++) {
            Location finalTeleport = new Location(center.getWorld(), 0, 0, 0);
            boolean invalid = true;
            for (int i = 0; i < ScatterManager.getInstance().getMaxTries(); i++) {
                //get a random angle between 0 and 2PI
                double randomAngle = getRandom().nextDouble() * MATH_TAU;
                //get a random radius for uniform circular distribution
                double newradius = params.getRadius() * Math.sqrt(getRandom().nextDouble());

                //Convert back to cartesian
                double xcoord = MathsHelper.getXFromRadians(newradius, randomAngle) + center.getBlockX();
                double zcoord = MathsHelper.getZFromRadians(newradius, randomAngle) + center.getBlockZ();

                //get the center of the block/s
                xcoord = Math.round(xcoord) + X_OFFSET;
                zcoord = Math.round(zcoord) + Z_OFFSET;

                //set the locations coordinates
                finalTeleport.setX(xcoord);
                finalTeleport.setZ(zcoord);

                //get the highest block in the Y coordinate
                ServerUtil.setYHighest(finalTeleport);

                if (isLocationTooClose(finalTeleport, locations, params.getMinimumDistance())) {
                    continue;
                }

                //if the block isnt allowed get a new coord
                if (!params.blockAllowed(finalTeleport.getBlock().getType())) {
                    continue;
                }

                //valid teleport, exit
                invalid = false;
                break;
            }
            if (invalid) {
                throw new MaxAttemptsReachedException();
            }
            locations.add(finalTeleport);
        }
        return locations;
    }
}
