package uk.co.eluinhost.UltraHardcore.scatter;

import java.util.List;

public final class ScatterParams {

	private int radius;
	private int x;
	private int z;
	private String world;
	private List<Integer> allowedBlocks = null;
	private int minDistance = 0;
	
	public ScatterParams(String world, int x,int z,int radius){
		setWorld(world);
		setRadius(radius);
		setX(x);
		setZ(z);
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
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
	public int getMinDistance() {
		return minDistance;
	}
	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}
	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	public int getMinDistanceSquared(){
		return getMinDistance()*getMinDistance();
	}
}
