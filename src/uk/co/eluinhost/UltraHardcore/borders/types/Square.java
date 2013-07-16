package uk.co.eluinhost.UltraHardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import uk.co.eluinhost.UltraHardcore.borders.BorderParams;
import uk.co.eluinhost.UltraHardcore.borders.WorldEditBorder;

public class Square extends WorldEditBorder{

	@Override
	protected void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException {
		Vector pos1 = new Vector(
			bp.getX()+bp.getRadius(),
			256,
			bp.getZ()+bp.getRadius());
		Vector pos2 = new Vector(
			bp.getX()-bp.getRadius(),
			0,
			bp.getZ()-bp.getRadius());
		es.makeCuboidWalls(new CuboidRegion(pos1, pos2)
						,new SingleBlockPattern(
						new BaseBlock(bp.getBlockID(),bp.getBlockMeta())));
	}

	@Override
	public String getID() {
		return "Square";
	}

	@Override
	public String getDescription() {
		return "Creates a square wall around the map";
	}
}
