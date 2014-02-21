package uk.co.eluinhost.ultrahardcore.borders;

/**
 * Holds all the information about a border
 * @author ghowden
 *
 */
public class BorderParams {

	private int x;
	private int z;
	private String worldName;
	private int blockID;
	private int blockMeta;
	private int radius;
	private String typeID;
	
	public BorderParams(int x,int z,int radius,String typeID,String worldName,int blockID,int blockMeta){
		setX(x);
		setZ(z);
		setWorldName(worldName);
		setBlockID(blockID);
		setBlockMeta(blockMeta);
		setRadius(radius);
		setTypeID(typeID);
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
	public String getWorldName() {
		return worldName;
	}
	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}
	public int getBlockID() {
		return blockID;
	}
	public void setBlockID(int blockID) {
		this.blockID = blockID;
	}
	public int getBlockMeta() {
		return blockMeta;
	}
	public void setBlockMeta(int blockMeta) {
		this.blockMeta = blockMeta;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public String getTypeID() {
		return typeID;
	}

	public void setTypeID(String typeID) {
		this.typeID = typeID;
	}
}
