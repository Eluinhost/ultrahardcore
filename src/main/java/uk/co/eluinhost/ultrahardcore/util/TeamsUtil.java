package uk.co.eluinhost.ultrahardcore.util;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamsUtil {

    private final Scoreboard m_mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    /**
     * Empties all teams and removes the teams from the scoreboard
     * @param tellPlayer whether to tell the player they were removed or not
     */
    public void clearTeams(boolean tellPlayer) {
        for (Team t : m_mainScoreboard.getTeams()) {
            for (OfflinePlayer p : t.getPlayers()) {
                t.removePlayer(p);
                if (tellPlayer && p.isOnline()) {
                    p.getPlayer().sendMessage(ChatColor.GOLD + "You were removed from your team");
                }
            }
            try {
                t.unregister();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    /**
     * @return a set of all the teams
     */
    public Set<Team> getAllTeams(){
        return m_mainScoreboard.getTeams();
    }

    /**
     * Get the team with the given name
     * @param name the team name to check for
     * @return the team if exists, null otherwise
     */
    public Team getTeam(String name){
        return m_mainScoreboard.getTeam(name);
    }

    /**
     * Remove the team from the scoreboard
     * @param name the team name to remove
     */
    public void removeTeam(String name){
        Team team = getTeam(name);
        if(team != null){
            team.unregister();
        }
    }

    /**
     * Registers a Team on this Scoreboard
     *
     * @param name Team name
     * @return registered Team
     * @throws IllegalArgumentException if name is null or if team by that name already exists
     */
    public Team registerNewTeam(String name) {
        return m_mainScoreboard.registerNewTeam(name);
    }

    /**
     * Gets a player's Team on this Scoreboard
     *
     * @param player the player to search for
     * @return the player's Team or null if the player is not on a team
     * @throws IllegalArgumentException if player is null
     */
    public Team getPlayersTeam(OfflinePlayer player){
        return m_mainScoreboard.getPlayerTeam(player);
    }

    /**
     * Formatted team
     *
     * @param t the team
     * @return the team formatted
     */
    public static String teamToString(Team t) {
        Set<OfflinePlayer> ps = t.getPlayers();
        StringBuilder buffer = new StringBuilder(String.valueOf(ChatColor.GOLD))
                .append(t.getDisplayName())
                .append(" (")
                .append(t.getName())
                .append(")")
                .append(": ")
                .append(ChatColor.RED);
        if (ps.isEmpty()) {
            buffer.append("No Players!");
        } else {
            for (OfflinePlayer p : ps) {
                buffer.append(p.getName())
                        .append(", ");
            }
            buffer.delete(buffer.length() - 2, buffer.length());
        }
        return buffer.toString();
    }

    /**
     * Removes all the players from the list that are in a team
     *
     * @param players the array list of player to remove teamed players from
     */
    public void removePlayersInATeam(Iterable<Player> players) {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (m_mainScoreboard.getPlayerTeam(p) != null) {
                it.remove();
            }
        }
    }

    /**
     * Gets the next UHCxxx team available
     *
     * @param onlyMakeNew only make a new team, dont just return an empty team
     * @return Team first UHCxxx team
     */
    public Team getNextAvailableTeam(boolean onlyMakeNew) {
        int count = 0;
        while (true) {
            Team thisteam = m_mainScoreboard.getTeam("UHC" + count);
            if (thisteam != null) {
                if (!onlyMakeNew && thisteam.getSize() == 0) {
                    return thisteam;
                }
                count++;
            } else {
                Team newTeam = m_mainScoreboard.registerNewTeam("UHC" + count);
                newTeam.setDisplayName(new WordsUtil().getRandomTeamName());
                return newTeam;
            }
        }
    }

    /**
     * @param name the team name to check for
     * @return true if exists, false otherwise
     */
    public boolean teamExists(String name) {
        return m_mainScoreboard.getTeam(name) != null;
    }

    /**
     * @param p the player to remove
     * @param tellPlayer whether to tell the player they were removed or not
     * @param tellTeam whether to tell the team the player was removed or not
     * @return true if removed, false otherwise
     */
    public boolean removePlayerFromTeam(OfflinePlayer p, boolean tellPlayer, boolean tellTeam) {
        Team t = m_mainScoreboard.getPlayerTeam(p);
        if (t == null) {
            return false;
        }
        t.removePlayer(p);
        if (tellPlayer) {
            Player player = p.getPlayer();
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "You were removed from your team");
            }
        }
        if (tellTeam) {
            for (OfflinePlayer op : t.getPlayers()) {
                Player player = op.getPlayer();
                if (player != null) {
                    player.sendMessage(ChatColor.GOLD + p.getName() + " left your team");
                }
            }
        }
        return true;
    }

    /**
     * @param player the player to add
     * @param t the team to add to
     * @param tellPlayer whether to tell the player they were removed or not
     * @param tellTeam whether to tell the team the player was removed or not
     */
    public static void playerJoinTeam(OfflinePlayer player, Team t, boolean tellPlayer, boolean tellTeam) {
        t.addPlayer(player);
        if (tellTeam) {
            for (OfflinePlayer op : t.getPlayers()) {
                Player p = op.getPlayer();
                if (p != null) {
                    p.sendMessage(ChatColor.GOLD + player.getName() + " joined your team!");
                }
            }
        }
        if (tellPlayer) {
            Player p = player.getPlayer();
            if (p != null) {
                p.sendMessage(ChatColor.GOLD + "You joined the team " + t.getDisplayName() + " (" + t.getName() + ")");
            }
        }
    }

    /**
     * Empties all the teams
     * @param tellPlayer whether to tell the player they were removed or not
     */
    public void emptyTeams(boolean tellPlayer) {
        for (Team t : m_mainScoreboard.getTeams()) {
            for (OfflinePlayer p : t.getPlayers()) {
                t.removePlayer(p);
                if (p.isOnline()) {
                    p.getPlayer().sendMessage(ChatColor.GOLD + "You were removed from your team");
                }
            }
        }
    }
}
