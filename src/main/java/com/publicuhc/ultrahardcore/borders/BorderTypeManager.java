/*
 * BorderTypeManager.java
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

package com.publicuhc.ultrahardcore.borders;

import com.publicuhc.ultrahardcore.borders.exceptions.BorderIDConflictException;
import com.publicuhc.ultrahardcore.borders.types.Border;

import java.util.Collection;

public interface BorderTypeManager {

   /**
    * @param id border ID
    * @return border if exists, null if not
    */
    Border getBorderByID(String id);

    /**
     * Add the border to the list
     * @param border the border
     * @throws com.publicuhc.ultrahardcore.borders.exceptions.BorderIDConflictException if ID is already taken
     */
    void addBorder(Border border) throws BorderIDConflictException;

    /**
     * @return unmodifiable collection of border types
     */
    Collection<Border> getTypes();
}
