package uk.co.eluinhost.UltraHardcore.scatter;

import java.util.List;

public final class ScatterParams {

	private double radius;
	private double x;
	private double z;
	private String world;
	private List<Integer> allowedBlocks = null;
	private double minDistance = 0;
	
	public ScatterParams(String world, double x,double z,double radius){
		setWorld(world);
		setRadius(radius);
		setX(x);
		setZ(z);
	}

    public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	public List<Integer> getAllowedBlocks() {
		return allowedBlocks;
	}
	public void setAllowedBlocks(List<Integer> allowedBlocks) {
		this.allowedBlocks = allowedBlocks;
	}
	public boolean blockIDAllowed(Integer i){
        return allowedBlocks == null || getAllowedBlocks().contains(i);
    }
	public double getMinDistance() {
		return minDistance;
	}
	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}
	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}
}
