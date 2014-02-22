package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.Material;

import java.util.List;

//TODO store a location instead of multiple params?
public class ScatterParams {

    private final double m_radius;
    private final double m_centerX;
    private final double m_centerZ;
    private final String m_world;
    private List<Material> m_allowedMaterials;
    private double m_minimumDistance;

    public ScatterParams(String world, double x, double z, double radius) {
        m_world = world;
        m_radius = radius;
        m_centerX = x;
        m_centerZ = z;
    }

    public double getRadius() {
        return m_radius;
    }

    public double getCenterX() {
        return m_centerX;
    }

    public double getCenterZ() {
        return m_centerZ;
    }

    public void setAllowedBlocks(List<Material> allowedBlocks) {
        m_allowedMaterials = allowedBlocks;
    }

    public double getMinDistance() {
        return m_minimumDistance;
    }

    public void setMinDistance(double minDistance) {
        m_minimumDistance = minDistance;
    }

    public String getWorld() {
        return m_world;
    }

    public boolean blockAllowed(Material material) {
        for (Material m2 : m_allowedMaterials) {
            if (material.name().equals(m2.name())) {
                return true;
            }
        }
        return false;
    }
}
