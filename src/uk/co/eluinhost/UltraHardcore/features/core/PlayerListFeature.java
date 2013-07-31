package uk.co.eluinhost.UltraHardcore.features.core;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

/**
 * PlayerListHandler
 * 
 * Handles the playerlist health numbers for players
 * 
 * @author ghowden
 *
 */
public class PlayerListFeature extends UHCFeature {
	
	public PlayerListFeature(boolean enabled){
		super(enabled);
		setFeatureID("PlayerList");
		setDescription("Player's health shown in player list and under their name");
	}

	//the internal bukkit id for the task
	private static int task_id = -1;
	
	//the list of players and their health that we are handling
	private static HashMap<String,Double> players = new HashMap<String,Double>();

    private static int health_scaling = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.PLAYER_LIST_SCALING);
    private static boolean round_health = ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.PLAYER_LIST_ROUND_HEALTH);
	
	private static Scoreboard board = null;
	static{
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		board = manager.getMainScoreboard();
	}
	private static Objective obj_player_list = null;
	private static Objective obj_player_name = null;
	
	//update the players name in the list with the following health number
	public static void updatePlayerListHealth(Player player, double d) {
		String new_name = ChatColor.stripColor(player.getDisplayName());
		if(ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.PLAYER_LIST_COLOURS)){
			new_name = new_name.substring(0, Math.min(new_name.length(), 14));
            if(!player.hasPermission(PermissionNodes.PLAYER_LIST_HEALTH)){
                new_name = ChatColor.BLUE+new_name;
                d = 0;
            }else if(d <= 6){
				new_name = ChatColor.RED+new_name;
			}else if(d <=12){
				new_name = ChatColor.YELLOW+new_name;
			}else{
				new_name = ChatColor.GREEN+new_name;
			}
		}else{
            new_name = new_name.substring(0,Math.min(new_name.length(),16));
        }
		player.setPlayerListName(new_name);
        if(round_health){
            d = Math.ceil(d);
        }
		obj_player_list.getScore(Bukkit.getOfflinePlayer(new_name)).setScore((int) (d*health_scaling));
		obj_player_name.getScore(Bukkit.getOfflinePlayer(ChatColor.stripColor(player.getDisplayName()))).setScore((int) (d*health_scaling));
	}
	
	public static void updatePlayers(Player[] onlinePlayers) {
		for(Player p : onlinePlayers){
			Double i = players.get(p.getDisplayName());
			if(i == null){
				i = (double) 0;
				players.put(p.getDisplayName(), i);
			}
			if(p.getHealth()!=i){
				updatePlayerListHealth(p,p.getHealth());
			}
		}
	}

	@Override
	public void enableFeature() {
		task_id = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(), new Runnable(){
				@Override
				public void run() {
					updatePlayers(Bukkit.getOnlinePlayers());
				}
			},
			1,
			ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.PLAYER_LIST_DELAY));
		initializeScoreboard();
	}
	
	@Override
	public void disableFeature() {
		//disable the task if its running
		if(task_id >= 0){
			Bukkit.getScheduler().cancelTask(task_id);
			task_id = -1;
		}
		if(board != null){
			board.clearSlot(DisplaySlot.PLAYER_LIST);
			board.clearSlot(DisplaySlot.BELOW_NAME);
			for(Player p : Bukkit.getOnlinePlayers()){
				p.setPlayerListName(p.getDisplayName());
			}
		}
	}
	
	private void initializeScoreboard(){
		try{
	    	board.registerNewObjective("UHCHealth", "dummy");
	    }catch(IllegalArgumentException ignored){}
	    try{
	    	board.registerNewObjective("UHCHealthName", "dummy");
	    }catch(IllegalArgumentException ignored){}
	    
	    obj_player_list = board.getObjective("UHCHealth");
	    obj_player_name = board.getObjective("UHCHealthName");
	    obj_player_name.setDisplayName(ChatColor.RED+"\u2665");
	    obj_player_list.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	    if(ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.PLAYER_LIST_UNDER_NAME)){
	    	obj_player_name.setDisplaySlot(DisplaySlot.BELOW_NAME);
	    }else{
	    	Objective o = board.getObjective(DisplaySlot.BELOW_NAME);
	    	if(o != null && o.getName().equals("UHCHealthName")){
	    		board.clearSlot(DisplaySlot.BELOW_NAME);
	    	}
	    }
	}
}
