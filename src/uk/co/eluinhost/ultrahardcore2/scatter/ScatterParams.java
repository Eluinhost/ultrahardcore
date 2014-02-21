package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.Material;

import java.util.List;

public final class ScatterParams {

	private double radius;
	private double x;
	private double z;
	private String world;
	private List<Material> allowedBlocks = null;
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
	public void setAllowedBlocks(List<Material> allowedBlocks) {
		this.allowedBlocks = allowedBlocks;
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

    public boolean blockAllowed(Material m) {
        for(Material m2 : allowedBlocks){
            if(m.name().equals(m2.name())){
                return true;
            }
        }
        return false;
    }
}
