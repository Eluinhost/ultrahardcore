package com.publicuhc.ultrahardcore.borders;

import org.bukkit.Location;
import com.publicuhc.ultrahardcore.borders.exceptions.TooManyBlocksException;
import com.publicuhc.ultrahardcore.borders.types.Border;
import org.bukkit.Material;

public class BorderCreator {

    private Border m_border;
    private int m_radius = 0;
    private Location m_center = null;
    private int m_blockMeta = 0;
    private Material m_blockID;

    /**
     * Create a new creator for the given border type
     * @param border the border type to use
     */
    public BorderCreator(Border border){
        m_border = border;
    }

    /**
     * Create the actual border
     * @throws TooManyBlocksException when too many blocks would be changed
     */
    public void createBorder() throws TooManyBlocksException {
        m_border.build(m_center, m_radius, m_blockID, m_blockMeta);
    }

    /**
     * @return the border type to use
     */
    public Border getBorder() {
        return m_border;
    }

    /**
     * @param border the border type to use
     */
    public void setBorder(Border border) {
        m_border = border;
    }

    /**
     * @return the radius of the border
     */
    public int getRadius() {
        return m_radius;
    }

    /**
     * @param radius the radius of the border
     */
    public void setRadius(int radius) {
        m_radius = radius;
    }

    /**
     * @return the center location for the border
     */
    public Location getCenter() {
        return m_center;
    }

    /**
     * @param center the center location for the border
     */
    public void setCenter(Location center) {
        m_center = center;
    }

    /**
     * @return the block data value
     */
    public int getBlockMeta() {
        return m_blockMeta;
    }

    /**
     * @param blockMeta the block data value
     */
    public void setBlockMeta(int blockMeta) {
        m_blockMeta = blockMeta;
    }

    /**
     * @return the block ID
     */
    public Material getBlockID() {
        return m_blockID;
    }

    /**
     * @param mat the material
     */
    public void setBlockID(Material mat) {
        m_blockID = mat;
    }
}
