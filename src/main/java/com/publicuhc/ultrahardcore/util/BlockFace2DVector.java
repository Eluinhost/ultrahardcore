/*
 * BlockFace2DVector.java
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

package com.publicuhc.ultrahardcore.util;

import org.bukkit.block.BlockFace;

public enum BlockFace2DVector{
    NORTH(BlockFace.NORTH),
    EAST(BlockFace.EAST),
    SOUTH(BlockFace.SOUTH),
    WEST(BlockFace.WEST),
    NORTH_EAST(BlockFace.NORTH_EAST),
    NORTH_NORTH_EAST(BlockFace.NORTH_NORTH_EAST),
    EAST_NORTH_EAST(BlockFace.EAST_NORTH_EAST),
    NORTH_WEST(BlockFace.NORTH_WEST),
    NORTH_NORTH_WEST(BlockFace.NORTH_NORTH_WEST),
    WEST_NORTH_WEST(BlockFace.WEST_NORTH_WEST),
    SOUTH_EAST(BlockFace.SOUTH_EAST),
    SOUTH_SOUTH_EAST(BlockFace.SOUTH_SOUTH_EAST),
    EAST_SOUTH_EAST(BlockFace.EAST_SOUTH_EAST),
    SOUTH_WEST(BlockFace.SOUTH_WEST),
    SOUTH_SOUTH_WEST(BlockFace.SOUTH_SOUTH_WEST),
    WEST_SOUTH_WEST(BlockFace.WEST_SOUTH_WEST);

    private final BlockFace m_blockFace;

    /**
     * @param blockFace the block face to represent
     */
    BlockFace2DVector(BlockFace blockFace) {
        m_blockFace = blockFace;
    }

    /**
     * @return amount of X coordinates
     */
    public int getX() {
        return -m_blockFace.getModX();
    }

    /**
     * @return amount of Z-coordinates
     */
    public int getZ() {
        return m_blockFace.getModZ();
    }

    /**
     * @return the block face
     */
    public BlockFace getBlockFace(){
        return m_blockFace;
    }

    /**
     * @return The angle between the x and z
     *
     */
    public double getAngle(){
        return StrictMath.atan2(getX(), getZ());
    }

    /**
     * Get the closest block face to the direction
     * @param lookAngle the direction
     * @return the closest block face
     */
    public static BlockFace getClosest(double lookAngle){
        BlockFace2DVector[] vectors = BlockFace2DVector.values();
        BlockFace2DVector best = vectors[0];
        double angle = Math.abs(best.getAngle());
        for(BlockFace2DVector bfv : BlockFace2DVector.values()){
            double a = lookAngle-bfv.getAngle();
            if(a > Math.PI*2){
                a -= Math.PI*2;
            }else if(a < 0){
                a += Math.PI*2;
            }
            if(Math.abs(a) < angle){
                best = bfv;
                angle = Math.abs(a);
            }
        }
        return best.getBlockFace();
    }
}