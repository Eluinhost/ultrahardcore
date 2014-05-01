/*
 * MathsHelper.java
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

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UtilityClass")
public final class MathsHelper {

    /**
     * Maths utilty methods
     */
    private MathsHelper() {}

    /**
     * Gets the Z distance from the radius and angle
     * @param radius the radius
     * @param angle the angle
     * @return the Z distance
     */
    public static double getZFromRadians(double radius, double angle) {
        return Math.round(radius * StrictMath.sin(angle));
    }

    /**
     * Gets the X distance from the radius and angle
     * @param radius the radius
     * @param angle the angle
     * @return the X distance
     */
    public static double getXFromRadians(double radius, double angle) {
        return radius * StrictMath.cos(angle);
    }

    /**
     * Splits the list into event sized lists
     * @param list the list to split
     * @param size the number of lists to make
     * @param <T> type
     * @return a list of the split lists
     */
    public static <T> List<List<T>> split(Iterable<T> list, int size) {
        List<List<T>> result = new ArrayList<List<T>>(size);

        for (int i = 0; i < size; i++) {
            result.add(new ArrayList<T>());
        }

        int index = 0;

        for (T t : list) {
            result.get(index).add(t);
            index = (index + 1) % size;
        }

        return result;
    }
}
