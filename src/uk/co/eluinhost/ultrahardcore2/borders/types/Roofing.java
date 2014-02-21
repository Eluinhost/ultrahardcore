package uk.co.eluinhost.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;

import uk.co.eluinhost.ultrahardcore.borders.BorderParams;
import uk.co.eluinhost.ultrahardcore.borders.WorldEditBorder;

public class Roofing extends WorldEditBorder{

	@Override
	protected void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException {
		es.makeCylinder(
				new Vector(bp.getX(),255,bp.getZ()),
				new SingleBlockPattern(new BaseBlock(bp.getBlockID(),bp.getBlockMeta())),
				bp.getRadius(),
				bp.getRadius(),
				1,
				true);
	}

	@Override
	public String getID() {
		return "Roof";
	}

	@Override
	public String getDescription() {
		return "Creates a circular roof over the area";
	}
}
