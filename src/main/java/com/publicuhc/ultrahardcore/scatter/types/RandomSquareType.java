package com.publicuhc.ultrahardcore.scatter.types;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import org.bukkit.Location;
import com.publicuhc.ultrahardcore.scatter.Parameters;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import com.publicuhc.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;
import com.publicuhc.ultrahardcore.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class RandomSquareType extends AbstractScatterType {

    private static final String SCATTER_ID = "RandomSquare";
    private static final String DESCRIPTION = "Uniformly scatter over a sqaure with sides length radius*2";

    /**
     * Scatter randomly within a square
     * @param scatterManager the scatter manager
     */
    @Inject
    protected RandomSquareType(ScatterManager scatterManager){
        super(scatterManager);
    }

    @Override
    public String getScatterID() {
        return SCATTER_ID;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public List<Location> getScatterLocations(Parameters params, int amount)
            throws MaxAttemptsReachedException {
        List<Location> locations = new ArrayList<Location>();
        Location center = params.getScatterLocation();
        double radius = params.getRadius();

        for (int k = 0; k < amount; k++) {
            Location finalTeleport = new Location(center.getWorld(), 0, 0, 0);
            boolean invalid = true;
            for (int i = 0; i < MAX_TRIES; i++) {
                //get a coords
                double xcoord = getRandom().nextDouble() * radius * 2;
                double zcoord = getRandom().nextDouble() * radius * 2;
                xcoord -= radius;
                zcoord -= radius;
                xcoord += center.getBlockX();
                zcoord += center.getBlockZ();

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
