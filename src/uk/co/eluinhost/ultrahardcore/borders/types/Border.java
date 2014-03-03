package uk.co.eluinhost.ultrahardcore.borders.types;

import org.bukkit.Location;
import uk.co.eluinhost.ultrahardcore.borders.exceptions.TooManyBlocksException;

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
     * @throws uk.co.eluinhost.ultrahardcore.borders.exceptions.TooManyBlocksException when too many blocks changed
     */
    void build(Location center, int radius, int blockID, int blockMeta) throws TooManyBlocksException;
}
