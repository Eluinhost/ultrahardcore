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
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class RandomSquareType extends AbstractScatterType {

    private static final String NAME = "RandomSquare";
    private static final String DESCRIPTION = "Uniformly scatter over a sqaure with sides length radius*2";

    public RandomSquareType(){
        super(NAME,DESCRIPTION);
    }

    @Override
    public List<Location> getScatterLocations(ScatterParams params, int amount)
            throws WorldNotFoundException, MaxAttemptsReachedException {
        World world = Bukkit.getWorld(params.getWorld());
        if (world == null) {
            throw new WorldNotFoundException();
        }
        AbstractList<Location> locations = new ArrayList<Location>();
        for (int k = 0; k < amount; k++) {
            Location finalTeleport = new Location(world, 0, 0, 0);
            boolean valid = false;
            for (int i = 0; i < ScatterManager.m_maxTries; i++) {
                //get a coords
                double xcoord = getRandom().nextDouble() * params.getRadius() * 2;
                double zcoord = getRandom().nextDouble() * params.getRadius() * 2;
                xcoord -= params.getRadius();
                zcoord -= params.getRadius();
                xcoord += params.getCenterX();
                zcoord += params.getCenterZ();

                //get the center of the block/s
                xcoord = Math.round(xcoord) + 0.5d;
                zcoord = Math.round(zcoord) + 0.5d;

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
