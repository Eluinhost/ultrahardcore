package uk.co.eluinhost.UltraHardcore.scatter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.exceptions.ScatterTypeConflictException;
import uk.co.eluinhost.UltraHardcore.scatter.types.EvenCircumferenceType;
import uk.co.eluinhost.UltraHardcore.scatter.types.ScatterType;
import uk.co.eluinhost.UltraHardcore.scatter.types.RandomCircularType;
import uk.co.eluinhost.UltraHardcore.scatter.types.RandomSquareType;

public class ScatterManager {

	public final static int MAX_TRIES = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.SCATTER_MAX_TRIES);
	
	private static ArrayList<ScatterType> scatterTypes = new ArrayList<ScatterType>();
	
	private static ScatterProtector sp = new ScatterProtector();
	
	static{
		Bukkit.getServer().getPluginManager().registerEvents(sp, UltraHardcore.getInstance());
		try {
			addScatterType(new EvenCircumferenceType());
			addScatterType(new RandomCircularType());
			addScatterType(new RandomSquareType());
		} catch (ScatterTypeConflictException e) {
			e.printStackTrace();
		}
	}
	
	public static void addScatterType(ScatterType type) throws ScatterTypeConflictException{
		for(ScatterType scatterType : scatterTypes){
			if(scatterType.getScatterName().equals(type.getScatterName())){
				throw new ScatterTypeConflictException();
			}
		}
		scatterTypes.add(type);
	}
	
	public static ScatterType getScatterType(String ID){
		for(ScatterType st : scatterTypes){
			if(st.getScatterName().equals(ID)){
				return st;
			}
		}
		return null;
	}
	
	public static ArrayList<ScatterType> getScatterTypes(){
		return scatterTypes;
	}
	
	public static List<String> getScatterTypeNames(){
		ArrayList<String> r = new ArrayList<String>();
		for(ScatterType st : scatterTypes){
			r.add(st.getScatterName());
		}
		return r;
	}
	
	public static void teleportSafe(Player p,Location loc){
		loc.getChunk().load(true);
		p.teleport(loc);
		sp.add(p.getName(),loc);
	}
	
	
}
