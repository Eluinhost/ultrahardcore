package com.publicuhc.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import org.bukkit.Location;
import org.bukkit.Material;

public class CylinderBorder extends WorldEditBorder {

    public static final int MAX_HEIGHT = 256;

    /**
     * Cylinder to max height
     */
    public CylinderBorder() {
        super("Cylinder", "Creates a cylinder wall around the map");
    }

    @Override
    public void createBorder(Location center, double radius, Material blockID, int blockMeta, EditSession es) throws MaxChangedBlocksException {
        //noinspection deprecation
        es.makeCylinder(
                new Vector(center.getBlockX(), 0, center.getBlockZ()),
                new SingleBlockPattern(new BaseBlock(blockID.getId(), blockMeta)),
                radius,
                radius,
                MAX_HEIGHT,
                false);
    }
}
