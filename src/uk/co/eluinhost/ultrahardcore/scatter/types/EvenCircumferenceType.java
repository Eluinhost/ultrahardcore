package uk.co.eluinhost.ultrahardcore.scatter.types;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterParams;
import uk.co.eluinhost.ultrahardcore.util.MathsHelper;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class EvenCircumferenceType extends AbstractScatterType {

    private static final String NAME = "EvenCircle";
    private static final String DESCRIPTION = "Puts players at even distances distance from each other along the circumference";

    private static final double TAU = Math.PI * 2.0D;
    public static final int WORLD_TOP_BLOCK = 255;
    public static final double X_OFFSET = 0.5d;
    public static final double Z_OFFSET = 0.5d;

    public EvenCircumferenceType(){
        super(NAME,DESCRIPTION);
    }

    @Override
    public List<Location> getScatterLocations(ScatterParams params, int amount)
            throws WorldNotFoundException, MaxAttemptsReachedException {
        //angular difference between players
        double increment = TAU / amount;

        //List of locations to return
        AbstractList<Location> locations = new ArrayList<Location>();

        //If the world isn't valid throw a fit
        World w = Bukkit.getWorld(params.getWorld());
        if (w == null) {
            throw new WorldNotFoundException();
        }

        //for all the things
        for (int i = 0; i < amount; i++) {

            //get which angle we are doing
            double angle = i * increment;

            //convert from radians
            double x = MathsHelper.getXFromRadians(params.getRadius(), angle);
            double z = MathsHelper.getZFromRadians(params.getRadius(), angle);

            //get the location with offset
            Location finalTeleport = new Location(w, 0, WORLD_TOP_BLOCK, 0);
            finalTeleport.setX(x + params.getX() + X_OFFSET);
            finalTeleport.setZ(z + params.getZ() + Z_OFFSET);

            ServerUtil.setYHighest(finalTeleport);

            //store it
            locations.add(finalTeleport);
        }
        return locations;
    }
}
