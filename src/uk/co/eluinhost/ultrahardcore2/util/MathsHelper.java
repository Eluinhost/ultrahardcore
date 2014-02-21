package uk.co.eluinhost.ultrahardcore.util;

import java.util.ArrayList;
import java.util.List;

public final class MathsHelper {

	public static double getZFromRadians(double radius,double angle){
		return Math.round(radius*Math.sin(angle));
	}
	public static double getXFromRadians(double radius, double angle){
		return radius*Math.cos(angle);
	}

	public static <T> List<List<T>> split(List<T> list, int size) {
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
