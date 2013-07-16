package uk.co.eluinhost.UltraHardcore.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamsUtil {

	private Pattern pat = Pattern.compile("UHC[\\d]++");
	private Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
	
	/**
	 * Clears all the teams
	 * @param allTeams flag whether to clear all teams
	 */
	public void clearTeams(boolean allTeams){
		for(Team t : sc.getTeams()){
			Matcher m = pat.matcher(t.getName());
			if(m.matches() || allTeams){
				for(OfflinePlayer p : t.getPlayers()){
					t.removePlayer(p);
					if(p.isOnline()){
						p.getPlayer().sendMessage(ChatColor.GOLD+"You were removed from your team");
					}	
				}
				try{
					t.unregister();
				}catch(IllegalStateException ignored){}
			}
		}
	}
	
	/**
	 * Formatted team
	 * @param t        the team
	 * @return         the team formatted
	 */
	public String teamToString(Team t){
		Set<OfflinePlayer> ps = t.getPlayers();
		StringBuilder buffer = new StringBuilder(ChatColor.GOLD+"")
		.append(t.getDisplayName())
		.append(" (")
		.append(t.getName())
		.append(")")
		.append(": ")
		.append(ChatColor.RED);
		if(ps.size()>0){
			for(OfflinePlayer p : ps){
				buffer.append(p.getName())
				      .append(", ");
			}
			buffer.delete(buffer.length()-2, buffer.length());
		}else{
			buffer.append("No Players!");
		}
		return buffer.toString();
	}
	
	/**
	 * sends the list of teams to the sender
	 * @param sender        the sender of the command
	 * @param allTeams      whether to show all teams
	 */
	public void sendTeams(CommandSender sender,boolean allTeams){
		Set<Team> teams = sc.getTeams();
		boolean oneFound = false;
		for(Team t : teams){
			Matcher m = pat.matcher(t.getName());
			if(m.matches() || allTeams){
				oneFound = true;
				sender.sendMessage(teamToString(t));
			}
		}
		if(!oneFound){
			sender.sendMessage(ChatColor.GOLD+"There are no "+(!allTeams?"UHC":"")+" teams defined yet!");
		}
	}
	
	/**
	 * Removes all the players already in a team from supplied list
	 * @param players      the array list of player to remove teamed players from
	 */
	public void removeAllInTeam(ArrayList<Player> players){
		Iterator<Player> it = players.iterator();
		while(it.hasNext()){
			Player p = it.next();
			Team t = sc.getPlayerTeam(p);
			if(t != null){
				it.remove();
			}
		}
	}
	
	/**
	 * Gets the next UHCxxx team available
	 * @return Team
	 */
	public Team getNextAvailableTeam(boolean onlyMakeNew){
		Team thisteam;
		int count = 0;
		while(true){
			thisteam = sc.getTeam("UHC"+count);
			if(thisteam != null){
				if(!onlyMakeNew && thisteam.getSize() == 0){
					return thisteam;
				}
				count++;
			}else{
				thisteam = sc.registerNewTeam("UHC"+count);
				thisteam.setDisplayName(WordsUtil.getRandomTeamName());
				return thisteam;
			}
		}
	}

    @SuppressWarnings("unused")
	public boolean teamExists(String name){
		return sc.getTeam(name) != null;
	}

	public boolean removePlayerFromTeam(OfflinePlayer p,int printflags){
		Team t = sc.getPlayerTeam(p);
		if(t == null){
			return false;
		}
		t.removePlayer(p);
		if(PrintFlags.printToPlayer(printflags)){
			Player player = p.getPlayer();
			if(player != null){
				player.sendMessage(ChatColor.GOLD+"You were removed from your team");
			}
		}
		if(PrintFlags.printToTeam(printflags)){
			for(OfflinePlayer op : t.getPlayers()){
				Player player = op.getPlayer();
				if(player != null){
					player.sendMessage(ChatColor.GOLD+p.getName()+" left your team");
				}
			}
		}
		return true;
	}

	public void playerJoinTeam(OfflinePlayer player, Team t,int flags){
		if(PrintFlags.printToTeam(flags)){
			for(OfflinePlayer op : t.getPlayers()){
				Player p = op.getPlayer();
				if(p != null){
					p.sendMessage(ChatColor.GOLD+player.getName()+" joined your team!");
				}
			}
		}
		t.addPlayer(player);
		if(PrintFlags.printToPlayer(flags)){
				Player p = player.getPlayer();
				if(p != null){
					p.sendMessage(ChatColor.GOLD+"You joined the team "+t.getDisplayName()+" ("+t.getName()+")");
				}
		}
	}
	

	public boolean isUHCTeam(String s){
		return pat.matcher(s).matches();
	}
	
	
	public static class PrintFlags{
        @SuppressWarnings("unused")
		public static final int NONE = 0;
		public static final int PLAYER = 1;
		public static final int TEAM = 2;
		public static final int BOTH = PLAYER | TEAM;
		
		public static boolean printToTeam(int flags){
			return (flags & TEAM) == TEAM;
		}
		public static boolean printToPlayer(int flags){
			return (flags & PLAYER) == PLAYER;
		}
	}


	public void emptyTeams(boolean allTeams) {
		for(Team t : sc.getTeams()){
			Matcher m = pat.matcher(t.getName());
			if(m.matches() || allTeams){
				for(OfflinePlayer p : t.getPlayers()){
					t.removePlayer(p);
					if(p.isOnline()){
						p.getPlayer().sendMessage(ChatColor.GOLD+"You were removed from your team");
					}	
				}
			}
		}
	}
}
