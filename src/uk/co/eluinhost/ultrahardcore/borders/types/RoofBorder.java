package uk.co.eluinhost.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;

import uk.co.eluinhost.ultrahardcore.borders.BorderParams;
import uk.co.eluinhost.ultrahardcore.borders.WorldEditBorder;

public class RoofBorder extends WorldEditBorder {

    public static final int Y_VALUE = 255;

    public RoofBorder() {
        super("Roof", "Creates a circular roof over the area");
    }

    @Override
    protected void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException {
        es.makeCylinder(
                new Vector(bp.getX(), Y_VALUE, bp.getZ()),
                new SingleBlockPattern(new BaseBlock(bp.getBlockID(), bp.getBlockMeta())),
                bp.getRadius(),
                bp.getRadius(),
                1,
                true);
    }
}
