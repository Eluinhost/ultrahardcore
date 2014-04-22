package com.publicuhc.ultrahardcore.scatter.types;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import org.bukkit.Location;
import com.publicuhc.ultrahardcore.scatter.Parameters;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import com.publicuhc.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;
import com.publicuhc.ultrahardcore.util.MathsHelper;
import com.publicuhc.ultrahardcore.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class EvenCircumferenceType extends AbstractScatterType {

    private static final String SCATTER_NAME = "EvenCircle";
    private static final String DESCRIPTION = "Puts players at even distances distance from each other along the circumference";

    /**
     * Represents scatter logic
     *
     * @param scatterManager the scatter manager
     */
    @Inject
    protected EvenCircumferenceType(ScatterManager scatterManager) {
        super(scatterManager);
    }

    @Override
    public String getScatterID() {
        return SCATTER_NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
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
