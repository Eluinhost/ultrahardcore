package uk.co.eluinhost.ultrahardcore.util;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UtilityClass")
public class MathsHelper {

    private MathsHelper() {}

    public static double getZFromRadians(double radius, double angle) {
        return Math.round(radius * StrictMath.sin(angle));
    }

    public static double getXFromRadians(double radius, double angle) {
        return radius * StrictMath.cos(angle);
    }

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
