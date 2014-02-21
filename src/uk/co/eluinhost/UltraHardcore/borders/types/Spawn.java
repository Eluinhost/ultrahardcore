package uk.co.eluinhost.ultrahardcore.borders.types;

import java.io.File;
import java.io.IOException;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.borders.BorderParams;
import uk.co.eluinhost.ultrahardcore.borders.WorldEditBorder;

public class Spawn extends WorldEditBorder{

	@Override
	protected void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException {
		File schem = new File(UltraHardcore.getInstance().getDataFolder(),"spawn.schematic");
		SchematicFormat me = SchematicFormat.getFormat(schem);
		try{
			CuboidClipboard cc = me.load(schem);
			cc.paste(es, new Vector(bp.getX(),200,bp.getZ()), true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getID() {
		return "Spawn";
	}

	@Override
	public String getDescription() {
		return "Creates a spawn for you!";
	}
}
