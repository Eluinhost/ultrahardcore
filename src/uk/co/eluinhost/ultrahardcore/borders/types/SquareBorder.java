package uk.co.eluinhost.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import uk.co.eluinhost.ultrahardcore.borders.BorderParams;

public class SquareBorder extends WorldEditBorder {

    public static final int MAX_HEIGHT = 256;

    public SquareBorder() {
        super("Square", "Creates a square wall around the map");
    }

    @Override
    public void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException {
        Vector pos1 = new Vector(
                bp.getX() + bp.getRadius(),
                MAX_HEIGHT,
                bp.getZ() + bp.getRadius());
        Vector pos2 = new Vector(
                bp.getX() - bp.getRadius(),
                0,
                bp.getZ() - bp.getRadius());
        es.makeCuboidWalls(new CuboidRegion(pos1, pos2)
                , new SingleBlockPattern(
                new BaseBlock(bp.getBlockID(), bp.getBlockMeta())));
    }
}
