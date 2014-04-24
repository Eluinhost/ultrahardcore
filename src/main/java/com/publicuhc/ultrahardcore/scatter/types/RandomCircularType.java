/*
 * RandomCircularType.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

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
public class RandomCircularType extends AbstractScatterType {

    private static final String SCATTER_NAME = "RandomCircle";
    private static final String DESCRIPTION = "Randomly distributes locations evenly over a circular area";

    /**
     * Scatters within a circle randomly
     * @param scatterManager the scatter manager
     */
    @Inject
    protected RandomCircularType(ScatterManager scatterManager){
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
        Location center = params.getScatterLocation();
        List<Location> locations = new ArrayList<Location>();
        for (int k = 0; k < amount; k++) {
            Location finalTeleport = new Location(center.getWorld(), 0, 0, 0);
            boolean invalid = true;
            for (int i = 0; i < MAX_TRIES; i++) {
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
