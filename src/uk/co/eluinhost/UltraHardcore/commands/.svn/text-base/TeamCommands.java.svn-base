package uk.co.eluinhost.UltraHardcore.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.util.MathsHelper;
import uk.co.eluinhost.UltraHardcore.util.ServerUtil;
import uk.co.eluinhost.UltraHardcore.util.TeamsUtil;
import uk.co.eluinhost.UltraHardcore.util.WordsUtil;
import uk.co.eluinhost.UltraHardcore.util.TeamsUtil.PrintFlags;

public class TeamCommands extends UHCCommand{

	private Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
	private TeamsUtil tu = new TeamsUtil();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(command.getName().equals("createteam")){
			if(!sender.hasPermission(PermissionNodes.RANDOM_TEAMS_CREATE)){
				sender.sendMessage(ChatColor.RED+"You don't have permission");
				return true;
			}
			if(args.length > 1){
				sender.sendMessage(ChatColor.RED+"Syntax: /createteam [teamID]");
				return true;
			}
			Team thisteam;
			if(args.length == 1){
				thisteam = sc.getTeam(args[0]);
				if(thisteam != null){
					sender.sendMessage(ChatColor.RED+"Team already exists!");
					return true;
				}
				thisteam = sc.registerNewTeam(args[0]);
				thisteam.setDisplayName(WordsUtil.getRandomTeamName());
			}else{
				thisteam = tu.getNextAvailableTeam(true);
			}
			sender.sendMessage(ChatColor.GOLD+"Team '"+thisteam.getDisplayName()+"' ("+thisteam.getName()+") created!");
			return true;
		}
		else if(command.getName().equals("removeteam")){
			if(args.length != 1){
				sender.sendMessage(ChatColor.RED+"Syntax: /removeteam teamID");
				return true;
			}
			Team team = sc.getTeam(args[0]);
			if(team == null){
				sender.sendMessage(ChatColor.RED+"That team doesn't exist");
				return true;
			}
			boolean allowed = false;
			if(sender.hasPermission(PermissionNodes.RANDOM_TEAMS_REMOVE_ALL)){
				allowed = true;
			}else if(tu.isUHCTeam(args[0])){
				if(sender.hasPermission(PermissionNodes.RANDOM_TEAMS_REMOVE_UHC)){
					allowed = true;
				}
			}
			if(!allowed){
				sender.sendMessage(ChatColor.RED+"You don't have permission to remove that team");
				return true;
			}
			for(OfflinePlayer p : team.getPlayers()){
				Player p1 = p.getPlayer();
				if(p1 != null){
					p1.sendMessage(ChatColor.GOLD+"Your team was disbanded");
				}
			}
			try{
				team.unregister();
			}catch(IllegalStateException ignored){}
			sender.sendMessage(ChatColor.GOLD+"Team removed!");
			return true;
		}
		else if(command.getName().equals("leaveteam")){
			OfflinePlayer p;
			if(args.length == 0){
				if(!sender.hasPermission(PermissionNodes.RANDOM_TEAMS_LEAVE_SELF)){
					sender.sendMessage(ChatColor.RED+"You don't have permission to leave your team");
					return true;
				}
				if(!(sender instanceof Player)){
					sender.sendMessage(ChatColor.RED+"Must specify a player to be removed or ran as a player");
					return true;
				}
				p = ((Player)sender);
			}
			else if(args.length == 1){
				if(!sender.hasPermission(PermissionNodes.RANDOM_TEAMS_LEAVE_OTHER)){
					sender.sendMessage(ChatColor.RED+"You don't have permission");
					return true;
				}
				p = Bukkit.getOfflinePlayer(args[0]);
			}else{
				sender.sendMessage(ChatColor.RED+"Syntax: leaveteam [playername]");
				return true;
			}
			boolean removed = tu.removePlayerFromTeam(p, PrintFlags.BOTH);
			if(!removed){
				sender.sendMessage(ChatColor.RED+(args.length == 1?p.getName()+" is":"You are")+" not in a team!");
			}else if(args.length == 1){
				sender.sendMessage(ChatColor.GOLD+"Player removed from their team");
			}
			return true;
		}
		else if(command.getName().equals("jointeam")){
			if(args.length < 1){
				sender.sendMessage(ChatColor.RED+"Syntax: /jointeam teamID [playername]");
				return true;
			}
			Team team = sc.getTeam(args[0]);
			if(team == null){
				sender.sendMessage(ChatColor.RED+"That team doesn't exist");
				return true;
			}
			boolean allowed = false;
			if(sender.hasPermission(PermissionNodes.RANDOM_TEAMS_JOIN_ALL)){
				allowed = true;
			}else if(tu.isUHCTeam(args[0])){
				if(sender.hasPermission(PermissionNodes.RANDOM_TEAMS_JOIN_UHC)){
					allowed = true;
				}
			}
			if(!allowed){
				sender.sendMessage(ChatColor.RED+"You don't have permission for that team");
				return true;
			}
			OfflinePlayer p;
			if(args.length == 1){
				if(!(sender instanceof Player)){
					sender.sendMessage(ChatColor.RED+"You need to be a player to run this command");
					return true;
				}
				p = (Player) sender;
			}else{
				if(!sender.hasPermission(PermissionNodes.RANDOM_TEAMS_JOIN_OTHER)){
					sender.sendMessage(ChatColor.RED+"You don't have permission put another player on a team");
					return true;
				}
				p = Bukkit.getOfflinePlayer(args[1]);
			}
			Team existingTeam = sc.getPlayerTeam(p);
			if(existingTeam != null){
				if(existingTeam.getName().equals(team.getName())){
					sender.sendMessage(ChatColor.RED+"Already in that team!");
					return true;
				}
				tu.removePlayerFromTeam(p, PrintFlags.TEAM);
			}
			tu.playerJoinTeam(p, team, PrintFlags.BOTH);
			if(args.length > 1){
				sender.sendMessage(ChatColor.GOLD+p.getName()+" added to the team "+team.getName());
			}
			return true;
		}
		else if(command.getName().equals("clearteams")){
			if(!sender.hasPermission(PermissionNodes.RANDOM_TEAMS_CLEAR)){
				sender.sendMessage(ChatColor.RED+"You don't have permission");
				return true;
			}
			boolean allTeams = args.length == 1 && args[0].equalsIgnoreCase("all");
			tu.clearTeams(allTeams);
			sender.sendMessage(ChatColor.GOLD+"All "+((allTeams)?"":"UHC")+" teams cleared!");
			return true;
		}else if(command.getName().equals("emptyteams")){
			if(!sender.hasPermission(PermissionNodes.RANDOM_TEAMS_EMPTY)){
				sender.sendMessage(ChatColor.RED+"You don't have permission");
				return true;
			}
			boolean allTeams = args.length == 1 && args[0].equalsIgnoreCase("all");
			tu.emptyTeams(allTeams);
			sender.sendMessage(ChatColor.GOLD+"All "+((allTeams)?"":"UHC")+" teams emptied!");
			return true;
		}else if(command.getName().equals("listteams")){
			if(!sender.hasPermission(PermissionNodes.LIST_TEAMS)){
				sender.sendMessage(ChatColor.RED+"You don't have permission");
				return true;
			}
			boolean allTeams = false;
			if(args.length > 0){
				if(args[0].equalsIgnoreCase("all")){
					allTeams = true;
				}else{
					Team t = sc.getTeam(args[0]);
					if(t == null){
						sender.sendMessage(ChatColor.RED+"Team not found!");
						return true;
					}
					sender.sendMessage(tu.teamToString(t));
					return true;
				}
			}
			tu.sendTeams(sender, allTeams);
			return true;
		}else if(command.getName().equals("randomteams")){
			if(!sender.hasPermission(PermissionNodes.RANDOM_TEAMS)){
				sender.sendMessage(ChatColor.RED+"You don't have permission");
				return true;
			}
			if(args.length == 1 || args.length == 2){
				ArrayList<Player> players = new ArrayList<Player>();
				if(args.length == 2){
					World w = Bukkit.getWorld(args[1]);
					if(w==null){
						sender.sendMessage(ChatColor.RED+"World not found!");
						return true;
					}
					players.addAll(w.getPlayers());
				}else{
                    Collections.addAll(players, Bukkit.getOnlinePlayers());
				}
				
				Collections.shuffle(players);
				
				tu.removeAllInTeam(players);
				
				int teamsToMake;
				try{
					teamsToMake = Integer.parseInt(args[0]);
				}catch(Exception ex){
					sender.sendMessage(ChatColor.RED+args[0]+" is not a number!");
					return true;
				}
				
				if(teamsToMake > players.size()){
					sender.sendMessage(ChatColor.RED+"Teams to create is greater than the number of people without a team!");
					return true;
				}
				
				if(teamsToMake <= 0){
					sender.sendMessage(ChatColor.RED+"Teams to create can not be zero or less!");
					return true;
				}
				
				List<List<Player>> teams = MathsHelper.split(players, teamsToMake);
				ArrayList<Team> finalTeams = new ArrayList<Team>();
				for(List<Player> team : teams){
					if(team == null){
						continue;
					}
					Team thisteam = tu.getNextAvailableTeam(false);
					finalTeams.add(thisteam);
					for(Player p : team){
						if(p != null){
							thisteam.addPlayer(p);
						}
					}
				}
				for(Team t : finalTeams){
					ArrayList<Player> team = new ArrayList<Player>();
					for(OfflinePlayer p : t.getPlayers()){
						if(p.getPlayer() != null){
							team.add(p.getPlayer());
						}
					}
					StringBuilder buffer = new StringBuilder(ChatColor.GOLD+"")
					.append("Your Team (")
					.append(t.getName())
					.append(" - ")
					.append(t.getDisplayName())
					.append("): ");
					for(Player p : team){
						buffer.append(p.getName()).append(", ");
					}
					buffer.delete(buffer.length()-2, buffer.length());
					String finalString = buffer.toString();
					for(Player p : team){
						p.sendMessage(finalString);
					}
				}
				sender.sendMessage(ChatColor.GOLD+"All teams randomized!");
				return true;
			}else{
				sender.sendMessage(ChatColor.RED+"Syntax: /randomteams numberofteams [worldname]");
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		ArrayList<String> r = new ArrayList<String>();
		if(command.getName().equals("createteam")){
			return r;
		}else if(command.getName().equals("removeteam")){
			if(args.length == 1){
				for(Team t : sc.getTeams()){
					r.add(t.getName());
				}
				return r;
			}
		}else if(command.getName().equals("leaveteam")){
			if(args.length == 1){
				return ServerUtil.getOnlinePlayers();
			}
			return r;
		}else if(command.getName().equals("jointeam")){
			if(args.length == 1){
				for(Team t : sc.getTeams()){
					r.add(t.getName());
				}
				return r;
			}
			if(args.length == 2){
				return ServerUtil.getOnlinePlayers();
			}
			return r;
		}else if(command.getName().equals("clearteams") 
				|| command.getName().equals("emptyteams")){
			if(args.length == 1){
				r.add("all");
				return r;
			}
			return r;
		}else if(command.getName().equals("listteams")){
			if(args.length == 1){
				for(Team t : sc.getTeams()){
					r.add(t.getName());
				}
				r.add("all");
			}
			return r;
		}else if(command.getName().equals("randomteams")){
			return r;
		}
		return r;
	}
	
	
}
