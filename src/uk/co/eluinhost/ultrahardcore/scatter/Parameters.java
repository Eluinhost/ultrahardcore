package uk.co.eluinhost.ultrahardcore.scatter;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Collection;
import java.util.LinkedList;

//TODO store a location instead of multiple params?
public class Parameters {

    private double m_radius = 0.0;
    private final Collection<Material> m_allowedMaterials = new LinkedList<Material>();
    private double m_minimumDistance = 0.0;
    private Location m_scatterLocation;

    /**
     * Make new parameters for scattering
     * @param scatterLocation the location to center around
     */
    public Parameters(Location scatterLocation) {
        m_scatterLocation = scatterLocation;
    }

    /**
     * Add a material to the allowed list
     * @param material the material
     * @return this
     */
    public Parameters addMaterial(Material material){
        m_allowedMaterials.add(material);
        return this;
    }

    /**
     * Adds all the materials to the allowed list
     * @param materials the allowed materials
     * @return this
     */
    public Parameters addMaterials(Collection<Material> materials){
        m_allowedMaterials.addAll(materials);
        return this;
    }

    /**
     * Does the params allow this material
     * @param material the material to check for
     * @return true if allowed or no materials set, false otherwise
     */
    public boolean blockAllowed(Material material) {
        if(m_allowedMaterials == null){
            return true;
        }

        for (Material m2 : m_allowedMaterials) {
            if (material.name().equals(m2.name())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the radius to scatter around
     */
    public double getRadius() {
        return m_radius;
    }

    /**
     * @param radius the radius to set to
     * @return this
     */
    public Parameters setRadius(double radius) {
        m_radius = radius;
        return this;
    }

    /**
     * @return The minimum distance between scatters
     */
    public double getMinimumDistance() {
        return m_minimumDistance;
    }

    /**
     * @param minimumDistance the minimum distance between scatters
     * @return this
     */
    public Parameters setMinimumDistance(double minimumDistance){
        m_minimumDistance = minimumDistance;
        return this;
    }

    /**
     * @return the center location
     */
    public Location getScatterLocation() {
        return m_scatterLocation;
    }

    /**
     * @param scatterLocation the location to center around
     * @return this
     */
    public Parameters setScatterLocation(Location scatterLocation) {
        m_scatterLocation = scatterLocation;
        return this;
    }
}
