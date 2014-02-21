package uk.co.eluinhost.ultrahardcore.borders;

/**
 * Holds all the information about a border
 *
 * @author ghowden
 */
public class BorderParams {

    private final int m_xCoord;
    private final int m_zCoord;
    private final String m_worldName;
    private final int m_blockID;
    private final int m_blockMeta;
    private final int m_radius;
    private final String m_typeID;

    public BorderParams(int x, int z, int radius, String typeID, String worldName, int blockID, int blockMeta) {
        m_xCoord = x;
        m_zCoord = z;
        m_worldName = worldName;
        m_blockID = blockID;
        m_blockMeta = blockMeta;
        m_radius = radius;
        m_typeID = typeID;
    }

    public int getX() {
        return m_xCoord;
    }

    public int getZ() {
        return m_zCoord;
    }

    public String getWorldName() {
        return m_worldName;
    }

    public int getBlockID() {
        return m_blockID;
    }

    public int getBlockMeta() {
        return m_blockMeta;
    }

    public int getRadius() {
        return m_radius;
    }

    public String getTypeID() {
        return m_typeID;
    }
}
