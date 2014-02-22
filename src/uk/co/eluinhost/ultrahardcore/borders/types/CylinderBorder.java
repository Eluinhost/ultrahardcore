package uk.co.eluinhost.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;

import uk.co.eluinhost.ultrahardcore.borders.BorderParams;

public class CylinderBorder extends WorldEditBorder {

    public static final int MAX_HEIGHT = 256;

    public CylinderBorder() {
        super("Cylinder", "Creates a cylinder wall around the map");
    }

    @Override
    public void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException {
        es.makeCylinder(
                new Vector(bp.getX(), 0, bp.getZ()),
                new SingleBlockPattern(new BaseBlock(bp.getBlockID(), bp.getBlockMeta())),
                bp.getRadius(),
                bp.getRadius(),
                MAX_HEIGHT,
                false);
    }
}
