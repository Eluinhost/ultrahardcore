package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.scatter.Parameters;
import uk.co.eluinhost.ultrahardcore.util.MathsHelper;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class EvenCircumferenceType extends AbstractScatterType {

    private static final String SCATTER_NAME = "EvenCircle";
    private static final String DESCRIPTION = "Puts players at even distances distance from each other along the circumference";

    /**
     * Scatter players evenly along the outside of the defined circle
     */
    public EvenCircumferenceType(){
        super(SCATTER_NAME,DESCRIPTION);
    }


    @Override
    public List<Location> getScatterLocations(Parameters params, int amount) throws MaxAttemptsReachedException {
        double radius = params.getRadius();

        //angular difference between players
        double increment = MATH_TAU / amount;

        //List of locations to return
        List<Location> locations = new ArrayList<Location>();

        //for all the things
        for (int i = 0; i < amount; i++) {

            //get which angle we are doing
            double angle = i * increment;

            //convert from radians
            double x = MathsHelper.getXFromRadians(radius, angle);
            double z = MathsHelper.getZFromRadians(radius, angle);

            //get the location with offset
            Location finalTeleport = params.getScatterLocation().clone();
            finalTeleport.setX(x + finalTeleport.getBlockX() + X_OFFSET);
            finalTeleport.setY(WORLD_TOP_BLOCK);
            finalTeleport.setZ(z + finalTeleport.getBlockZ() + Z_OFFSET);

            ServerUtil.setYHighest(finalTeleport);

            //store it
            locations.add(finalTeleport);
        }
        return locations;
    }
}
