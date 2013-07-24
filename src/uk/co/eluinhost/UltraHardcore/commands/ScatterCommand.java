package uk.co.eluinhost.UltraHardcore.commands;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.UltraHardcore.exceptions.WorldNotFoundException;
import uk.co.eluinhost.UltraHardcore.scatter.PlayerTeleportMapping;
import uk.co.eluinhost.UltraHardcore.scatter.ScatterManager;
import uk.co.eluinhost.UltraHardcore.scatter.ScatterParams;
import uk.co.eluinhost.UltraHardcore.scatter.types.ScatterType;
import uk.co.eluinhost.UltraHardcore.util.ServerUtil;

@SuppressWarnings("unused")
public class ScatterCommand extends UHCCommand {

	private LinkedList<PlayerTeleportMapping> remaining = new LinkedList<PlayerTeleportMapping>();
	private CommandSender commandIssuer = null;
	private int MAX_SCATTER_TRIES;
	private int jobID = -1;
	
	private static final String scatterSyntax = "/scatter typeID true/false radius[:mindist] world:[x,z] */player1 player2 player3";
	private static long SCATTER_DELAY;

	public ScatterCommand(){
		SCATTER_DELAY = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.SCATTER_DELAY);
		MAX_SCATTER_TRIES = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.SCATTER_MAX_ATTEMPTS);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(command.getName().equals("scatter")){
			if(!sender.hasPermission(PermissionNodes.SCATTER_COMMAND)){
				sender.sendMessage(ChatColor.RED+"You don't have permission "+PermissionNodes.SCATTER_COMMAND);
				return true;
			}
			if(remaining.size()>0){
				sender.sendMessage(ChatColor.RED+"Players are still being scattered from a previous command, please wait until it is done");
				return true;
			}
            FileConfiguration config = ConfigHandler.getConfig(ConfigHandler.MAIN);
			if(args.length == 1 && args[0].equalsIgnoreCase("default")){
				args = new String[5];
				args[0] = config.getString(ConfigNodes.SCATTER_DEFAULT_TYPE);
				args[1] = ""+config.getBoolean(ConfigNodes.SCATTER_DEFAULT_TEAMS);
				args[2] = config.getInt(ConfigNodes.SCATTER_DEFAULT_RADIUS)
						  +":"
						  +config.getInt(ConfigNodes.SCATTER_DEFAULT_MINRADIUS);
				args[3] = config.getString(ConfigNodes.SCATTER_DEFAULT_WORLD)
						  +":"
						  +config.getInt(ConfigNodes.SCATTER_DEFAULT_X)
						  +","
						  +config.getInt(ConfigNodes.SCATTER_DEFAULT_Z);
				args[4] = config.getString(ConfigNodes.SCATTER_DEFAULT_PLAYERS);
			}
			
			/*
			 * Get the types of scatter available
			 */
			if(args.length == 1 && args[0].equalsIgnoreCase("types")){
				ArrayList<ScatterType> scatterTypes = ScatterManager.getScatterTypes();
				if(scatterTypes.size() == 0){
					sender.sendMessage(ChatColor.RED+"No scatter types loaded!");
				}
				for(ScatterType st : scatterTypes){
					sender.sendMessage(ChatColor.GOLD+st.getScatterName()+ChatColor.GRAY+" - "+st.getDescription());
				}
				return true;
			}
			
			/*
			 * Check sane
			 */
			if(args.length < 5){
				sender.sendMessage(ChatColor.RED+"Syntax: "+scatterSyntax);
				return true;
			}
			
			/*
			 * Get the list of people to be scattered
			 */
			ArrayList<Player> to_be_scattered = new ArrayList<Player>();
			if(args[4].equals("*")){
                Collections.addAll(to_be_scattered, Bukkit.getOnlinePlayers());
			}else{
				for(int i = 4; i < args.length ; i++){
					Player p = Bukkit.getPlayer(args[i]);
					if(p == null){
						sender.sendMessage(ChatColor.RED+"Couldn't find player "+args[i]);
						continue;
					}
					to_be_scattered.add(p);
				}
			}
			if(to_be_scattered.size() == 0){
				sender.sendMessage(ChatColor.RED+"There were no player's to teleport");
				return true;
			}			
			
			/*
			 * get the world info and centre coords
			 */
			int x;
			int z;
			String[] parts = args[3].split(":");
			World w = Bukkit.getWorld(parts[0]);
			if(w == null){
				sender.sendMessage(ChatColor.RED+"World "+parts[0]+" not found!");
				return true;
			}
			if(parts.length == 2){
				String[] coords = parts[1].split(",");
				if(coords.length != 2){
					sender.sendMessage(ChatColor.RED+"The coords in "+args[3]+" are not recognized, use the format worldname:x,z");
					return true;
				}
				try{
					x = Integer.parseInt(coords[0]);
					z = Integer.parseInt(coords[1]);
				}catch(Exception ex){
					sender.sendMessage(ChatColor.RED+"One or more coordinates in "+args[3]+" were not detected as a number!");
					return true;
				}
			}else{
				x = w.getSpawnLocation().getBlockX();
				z = w.getSpawnLocation().getBlockZ();
			}
			
			
			/*
			 * get radius and min distance
			 */
			int mindist;
			int radius;
			String[] radiusparts = args[2].split(":");
			if(radiusparts.length == 2){
				try{
					mindist = Integer.parseInt(radiusparts[1]);
				}catch(Exception ex){
					sender.sendMessage(ChatColor.RED+"Minimum distance in "+args[2]+" not detected as a number!");
					return true;
				}
			}else{
                mindist = 0;
            }
			try{
				radius = Integer.parseInt(radiusparts[0]);
			}catch(Exception ex){
				sender.sendMessage(ChatColor.RED+"Radius in "+args[2]+" not detected as a number!");
				return true;
			}
			
			/*
			 * get the type of the scatter to do
			 */
			
			ScatterType type = ScatterManager.getScatterType(args[0]);
			if(type == null){
				sender.sendMessage(ChatColor.RED+"Scatter type "+args[0]+" not found. Type /scatter types to view the list of types");
				return true;
			}
			
			/*
			 * get whether to scatter in teams or not
			 */
			
			boolean team_scatter = Boolean.parseBoolean(args[1]);
			
			ScatterParams sp = new ScatterParams(w.getName(), x, z, radius);
			sp.setMinDistance(mindist);
			sp.setAllowedBlocks(config.getIntegerList(ConfigNodes.SCATTER_ALLOWED_BLOCKS));
				
			/*
			 * get the right amount of people to scatter
			 */
			HashMap<String,ArrayList<Player>> teams = new HashMap<String,ArrayList<Player>>();
			ArrayList<Player> noteams = new ArrayList<Player>();			
			
			if(team_scatter){
				Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
				for(Player p : to_be_scattered){
					Team t = sb.getPlayerTeam(p);
					if(t == null){
						noteams.add(p);
					}else{
						ArrayList<Player> team = teams.get(t.getName());
						if(team == null){
							ArrayList<Player> tteam = new ArrayList<Player>();
							tteam.add(p);
							teams.put(t.getName(),tteam);
						}else{
							team.add(p);
						}
					}
				}
			}else{
				noteams = to_be_scattered;
			}
			
			int number_of_ports = noteams.size()+teams.keySet().size();
			
			List<Location> teleports;
			try {
				teleports = type.getScatterLocations(sp, number_of_ports);
			} catch (WorldNotFoundException e) {
				sender.sendMessage(ChatColor.RED+"World not found!");
				return true;
			} catch (MaxAttemptsReachedException e) {
				sender.sendMessage(ChatColor.RED+"Max random scatter attempts reached ("+ScatterManager.MAX_TRIES+"), maybe try a lower/no minimum distance or making sure the allowed blocks accounts for your world");
				return true;
			}
			Iterator<Location> teleport_iterator = teleports.iterator();
			for(Player p : noteams){
				Location next = teleport_iterator.next();
				remaining.add(new PlayerTeleportMapping(p.getName(),next,null));
			}
			for(String s : teams.keySet()){
				Location next = teleport_iterator.next();
				for(Player p : teams.get(s)){
					remaining.add(new PlayerTeleportMapping(p.getName(),next,s));
				}
			}
			jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(), new ScatterRunable(), 0, SCATTER_DELAY);
			if(jobID != -1){
				commandIssuer = sender;
				sender.sendMessage(ChatColor.GOLD+"Starting to scatter players...");
			}else{
				sender.sendMessage(ChatColor.RED+"Error scheduling scatter");
				remaining.clear();
			}
			return true;
		}
		return false;
	}

	private class ScatterRunable implements Runnable{
		@Override
		public void run() {
			PlayerTeleportMapping ptm = remaining.pollFirst();
			if(ptm == null){
				Bukkit.getScheduler().cancelTask(jobID);
				try{
					commandIssuer.sendMessage(ChatColor.GOLD+"All players now scattered");
				}catch(Exception ignored){}
				commandIssuer = null;
				jobID = -1;
				return;
			}
			if(!teleportPlayer(ptm)){
				ptm.incrementAmountTried();
				if(ptm.getAmountTried()>MAX_SCATTER_TRIES){
					if(commandIssuer != null){
						commandIssuer.sendMessage(ChatColor.RED+"Failed to scatter "+ptm.getPlayerName()+" after "+MAX_SCATTER_TRIES+", giving up");
					}
				}else{
					remaining.add(ptm);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param ptm  the mapping to teleport the player using
	 * @return whether someone was teleported or not
	 */
	private boolean teleportPlayer(PlayerTeleportMapping ptm){
		Player p = Bukkit.getPlayerExact(ptm.getPlayerName());
		if(p == null){
			return false;
		}
		Location loc = ptm.getLocation();
		loc.add(0,2,0);
		ScatterManager.teleportSafe(p, loc);
		p.sendMessage(ChatColor.GOLD+"You were teleported "
		+((ptm.getTeamName()==null)?"solo":"with team "+ptm.getTeamName())
		+" to "+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		ArrayList<String> r = new ArrayList<String>();
		if(args.length == 1){
			r.add("types");
			r.addAll(ScatterManager.getScatterTypeNames());
			return r;
		}
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("types")){
				return r;
			}
			r.add("true");
			r.add("false");
			return r;
		}
		if(args.length == 3){
			r.add("radius:mindist");
			return r;
		}
		if(args.length == 4){
			return ServerUtil.getWorldNamesWithSpawn();
		}
		List<String> p = ServerUtil.getOnlinePlayers();
		p.add("*");
		return p;
	}	
	
 	
}
