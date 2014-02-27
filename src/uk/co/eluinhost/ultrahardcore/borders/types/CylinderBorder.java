package uk.co.eluinhost.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;

import org.bukkit.Location;
import uk.co.eluinhost.ultrahardcore.borders.BorderParams;

public class CylinderBorder extends WorldEditBorder {

    public static final int MAX_HEIGHT = 256;

    /**
     * Cylinder to max height
     */
    public CylinderBorder() {
        super("Cylinder", "Creates a cylinder wall around the map");
    }

    @Override
    public void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException {
        Location center = bp.getCenter();
        int radius = bp.getRadius();
        es.makeCylinder(
                new Vector(center.getBlockX(), 0, center.getBlockZ()),
                new SingleBlockPattern(new BaseBlock(bp.getBlockID(), bp.getBlockMeta())),
                radius,
                radius,
                MAX_HEIGHT,
                false);
    }
}
