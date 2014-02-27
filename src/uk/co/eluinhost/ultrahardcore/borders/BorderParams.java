package uk.co.eluinhost.ultrahardcore.borders;

import org.bukkit.Location;

/**
 * Holds all the information about a border
 *
 * @author ghowden
 */
public class BorderParams {

    private final Location m_location;
    private final int m_blockID;
    private final int m_blockMeta;
    private final int m_radius;
    private final String m_typeID;

    /**
     * Create new parameters for border creation
     * @param location the center location
     * @param typeID the ID of the border creator
     * @param radius the radius to give the creator
     * @param blockID the block ID to use
     * @param blockMeta the data value for the block
     */
    public BorderParams(Location location, String typeID, int radius, int blockID, int blockMeta) {
        m_location = location;
        m_blockID = blockID;
        m_blockMeta = blockMeta;
        m_radius = radius;
        m_typeID = typeID;
    }

    /**
     * @return center location
     */
    public Location getCenter() {
        return m_location;
    }

    /**
     * @return the block ID to use
     */
    public int getBlockID() {
        return m_blockID;
    }

    /**
     * @return the block data value
     */
    public int getBlockMeta() {
        return m_blockMeta;
    }

    /**
     * @return radius to use
     */
    public int getRadius() {
        return m_radius;
    }

    /**
     * @return the border ID
     */
    public String getTypeID() {
        return m_typeID;
    }
}
