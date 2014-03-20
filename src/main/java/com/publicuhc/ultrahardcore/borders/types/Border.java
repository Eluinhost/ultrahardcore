package com.publicuhc.ultrahardcore.borders.types;

import org.bukkit.Location;
import com.publicuhc.ultrahardcore.borders.exceptions.TooManyBlocksException;
import org.bukkit.Material;

public interface Border {
    /**
     * @return the ID of this border
     */
    String getID();
    /**
     * @return the description of this border
     */
    String getDescription();
    boolean equals(Object obj);
    int hashCode();

    /**
     * Create the border using the parameters
     * @param center the center location
     * @param radius the radius to use
     * @param blockID the block ID to use
     * @param blockMeta the block data value
     * @throws com.publicuhc.ultrahardcore.borders.exceptions.TooManyBlocksException when too many blocks changed
     */
    void build(Location center, double radius, Material blockID, int blockMeta) throws TooManyBlocksException;
}
