/*
 * RoofBorder.java
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

package com.publicuhc.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import org.bukkit.Location;
import org.bukkit.Material;

public class RoofBorder extends WorldEditBorder {

    public static final int Y_VALUE = 255;

    /**
     * Build a circular roof
     */
    public RoofBorder() {
        super("Roof", "Creates a circular roof over the area");
    }

    @Override
    public void createBorder(Location center, double radius, Material blockID, int blockMeta, EditSession es) throws MaxChangedBlocksException {
        //noinspection deprecation
        es.makeCylinder(
                new Vector(center.getBlockX(), Y_VALUE, center.getBlockZ()),
                new SingleBlockPattern(new BaseBlock(blockID.getId(), blockMeta)),
                radius,
                radius,
                1,
                true);
    }
}
