package uk.co.eluinhost.ultrahardcore.util;

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
