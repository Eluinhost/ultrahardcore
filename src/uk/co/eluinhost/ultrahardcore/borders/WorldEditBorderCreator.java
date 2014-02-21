package uk.co.eluinhost.ultrahardcore.borders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import uk.co.eluinhost.ultrahardcore.borders.types.Cylinder;
import uk.co.eluinhost.ultrahardcore.borders.types.Roofing;
import uk.co.eluinhost.ultrahardcore.borders.types.Square;
import uk.co.eluinhost.ultrahardcore.exceptions.borders.BorderTypeNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.worldedit.WorldEditMaxChangedBlocksException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;

/**
 * World edit references from BorderCreator to go here and stop classdefnotfound
 * @author ghowden
 *
 */
public abstract class WorldEditBorderCreator {
	
	private static HashMap<String,LinkedList<EditSession>> sessions = new HashMap<String,LinkedList<EditSession>>();

	private static ArrayList<WorldEditBorder> types = new ArrayList<WorldEditBorder>();
	public static ArrayList<WorldEditBorder> getTypes(){
		return types;
	}
	public static void build(BorderParams bp) throws WorldEditMaxChangedBlocksException, WorldNotFoundException, BorderTypeNotFoundException {
		try {
			World w = Bukkit.getWorld(bp.getWorldName());
			if(w==null){
				throw new WorldNotFoundException();
			}
			if(!sessions.containsKey(w.getName())){
				sessions.put(w.getName(),new LinkedList<EditSession>());
			}
			WorldEditBorder web = getBorderByID(bp.getTypeID());
			if(web == null){
				throw new BorderTypeNotFoundException();
			}
			LinkedList<EditSession> esl = sessions.get(w.getName());
			EditSession es = new EditSession(new BukkitWorld(w),Integer.MAX_VALUE);
			esl.add(es);
			web.createBorder(bp,es);
		} catch (MaxChangedBlocksException e) {
			throw new WorldEditMaxChangedBlocksException();
		}
	}
	
	public static List<String> getBorderIDs(){
		ArrayList<String> r = new ArrayList<String>();
		for(WorldEditBorder web : types){
			r.add(web.getID());
		}
		return r;
	}
	
	public static WorldEditBorder getBorderByID(String id){
		for(WorldEditBorder web : types){
			if(web.getID().equals(id)){
				return web;
			}
		}
		return null;
	}
	
	public static boolean undoForWorld(String world){
		LinkedList<EditSession> es = sessions.get(world);
		if(es == null || es.size() == 0){
			return false;
		}
		es.getLast().undo(es.getLast());
		es.removeLast();
		return true;
	}
	
	public static void initialize(){
		types.clear();
		types.add(new Cylinder());
		types.add(new Square());
		types.add(new Roofing());
		//not suitable for creation as of yet
		//types.add(new Spawn());
	}
}
