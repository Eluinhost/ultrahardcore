package uk.co.eluinhost.UltraHardcore.scatter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
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
	public final static int MAX_ATTEMPTS = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.SCATTER_MAX_ATTEMPTS);
    public final static int SCATTER_DELAY = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.SCATTER_DELAY);

	private static ArrayList<ScatterType> scatterTypes = new ArrayList<ScatterType>();
	
	private static ScatterProtector sp = new ScatterProtector();

    private static LinkedList<PlayerTeleportMapping> remainingTeleports = new LinkedList<PlayerTeleportMapping>();

    private static int jobID = -1;
    private static CommandSender commandIssuer = null;

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

    public static boolean isScatterInProgress(){
        return remainingTeleports.size()!=0;
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

	public static void addTeleportMappings(ArrayList<PlayerTeleportMapping> ptm,CommandSender sender){
        if(jobID == -1){
            remainingTeleports.addAll(ptm);
            jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(), new ScatterRunable(), 0, SCATTER_DELAY);
            if(jobID != -1){
                commandIssuer = sender;
                sender.sendMessage("Starting to scatter all players, teleports are " + ScatterManager.SCATTER_DELAY + " ticks apart");
            }else{
                sender.sendMessage(ChatColor.RED + "Error scheduling scatter");
                remainingTeleports.clear();
            }
        }
    }

    public static LinkedList<PlayerTeleportMapping> getRemainingTeleports(){
        return remainingTeleports;
    }

    private static boolean teleportPlayer(PlayerTeleportMapping ptm){
        Player p = Bukkit.getPlayerExact(ptm.getPlayerName());
        if(p == null){
            return false;
        }
        Location loc = ptm.getLocation();
        loc.add(0,2,0);
        teleportSafe(p, loc);
        p.sendMessage(ChatColor.GOLD+"You were teleported "
                +((ptm.getTeamName()==null)?"solo":"with team "+ptm.getTeamName())
                +" to "+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ());
        return true;
    }

    private static class ScatterRunable implements Runnable{
        @Override
        public void run() {
            PlayerTeleportMapping ptm = remainingTeleports.pollFirst();
            if(ptm == null){
                try{
                    commandIssuer.sendMessage(ChatColor.GOLD + "All players now scattered!");
                }catch(Exception ignored){}
                commandIssuer = null;
                Bukkit.getScheduler().cancelTask(jobID);
                jobID = -1;
                return;
            }
            if(!teleportPlayer(ptm)){
                ptm.incrementAmountTried();
                if(ptm.getAmountTried()>MAX_ATTEMPTS){
                    if(commandIssuer != null){
                        commandIssuer.sendMessage(ChatColor.RED + "Failed to scatter " + ptm.getPlayerName() + " after " + MAX_ATTEMPTS + ", giving up");
                    }
                }else{
                    remainingTeleports.add(ptm);
                }
            }
        }
    }
}
