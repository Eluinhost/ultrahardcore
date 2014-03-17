package uk.co.eluinhost.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import org.bukkit.Location;

public class RoofBorder extends WorldEditBorder {

    public static final int Y_VALUE = 255;

    /**
     * Build a circular roof
     */
    public RoofBorder() {
        super("Roof", "Creates a circular roof over the area");
    }

    @Override
    public void createBorder(Location center, int radius, int blockID, int blockMeta, EditSession es) throws MaxChangedBlocksException {
        es.makeCylinder(
                new Vector(center.getBlockX(), Y_VALUE, center.getBlockZ()),
                new SingleBlockPattern(new BaseBlock(blockID, blockMeta)),
                radius,
                radius,
                1,
                true);
    }
}
