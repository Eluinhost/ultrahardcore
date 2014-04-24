/*
 * Border.java
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

import org.bukkit.Location;
import com.publicuhc.ultrahardcore.borders.exceptions.TooManyBlocksException;
import org.bukkit.Material;

public interface Border {
    /**
     * @return the ID of this border
     */
    String getID();
    /**
     * @return the description of this border
     */
    String getDescription();
    boolean equals(Object obj);
    int hashCode();

    /**
     * Create the border using the parameters
     * @param center the center location
     * @param radius the radius to use
     * @param blockID the block ID to use
     * @param blockMeta the block data value
     * @throws com.publicuhc.ultrahardcore.borders.exceptions.TooManyBlocksException when too many blocks changed
     */
    void build(Location center, double radius, Material blockID, int blockMeta) throws TooManyBlocksException;
}
