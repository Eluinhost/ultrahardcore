package uk.co.eluinhost.ultrahardcore.util;

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

    private final Pattern m_teamNamePattern = Pattern.compile("UHC[\\d]++");
    private final Scoreboard m_mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    /**
     * Clears all the teams
     *
     * @param allTeams flag whether to clear all teams
     */
    public void clearTeams(boolean allTeams) {
        for (Team t : m_mainScoreboard.getTeams()) {
            Matcher matcher = m_teamNamePattern.matcher(t.getName());
            if (matcher.matches() || allTeams) {
                for (OfflinePlayer p : t.getPlayers()) {
                    t.removePlayer(p);
                    if (p.isOnline()) {
                        p.getPlayer().sendMessage(ChatColor.GOLD + "You were removed from your team");
                    }
                }
                try {
                    t.unregister();
                } catch (IllegalStateException ignored) {
                }
            }
        }
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
     * sends the list of teams to the sender
     *
     * @param sender   the sender of the command
     * @param allTeams whether to show all teams
     */
    public void sendTeams(CommandSender sender, boolean allTeams) {
        Set<Team> teams = m_mainScoreboard.getTeams();
        boolean oneFound = false;
        for (Team t : teams) {
            Matcher matcher = m_teamNamePattern.matcher(t.getName());
            if (matcher.matches() || allTeams) {
                oneFound = true;
                sender.sendMessage(teamToString(t));
            }
        }
        if (!oneFound) {
            sender.sendMessage(ChatColor.GOLD + "There are no " + (allTeams ? "" : "UHC") + " teams defined yet!");
        }
    }

    /**
     * Removes all the players already in a team from supplied list
     *
     * @param players the array list of player to remove teamed players from
     */
    public void removeAllInTeam(ArrayList<Player> players) {
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
     * @return Team
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
                newTeam.setDisplayName(WordsUtil.getRandomTeamName());
                return newTeam;
            }
        }
    }

    @SuppressWarnings("unused")
    public boolean teamExists(String name) {
        return m_mainScoreboard.getTeam(name) != null;
    }

    public boolean removePlayerFromTeam(OfflinePlayer p, int printflags) {
        Team t = m_mainScoreboard.getPlayerTeam(p);
        if (t == null) {
            return false;
        }
        t.removePlayer(p);
        if (PrintFlags.canPrintToPlayer(printflags)) {
            Player player = p.getPlayer();
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "You were removed from your team");
            }
        }
        if (PrintFlags.canPrintToTeam(printflags)) {
            for (OfflinePlayer op : t.getPlayers()) {
                Player player = op.getPlayer();
                if (player != null) {
                    player.sendMessage(ChatColor.GOLD + p.getName() + " left your team");
                }
            }
        }
        return true;
    }

    public static void playerJoinTeam(OfflinePlayer player, Team t, int flags) {
        if (PrintFlags.canPrintToTeam(flags)) {
            for (OfflinePlayer op : t.getPlayers()) {
                Player p = op.getPlayer();
                if (p != null) {
                    p.sendMessage(ChatColor.GOLD + player.getName() + " joined your team!");
                }
            }
        }
        t.addPlayer(player);
        if (PrintFlags.canPrintToPlayer(flags)) {
            Player p = player.getPlayer();
            if (p != null) {
                p.sendMessage(ChatColor.GOLD + "You joined the team " + t.getDisplayName() + " (" + t.getName() + ")");
            }
        }
    }


    public boolean isUHCTeam(CharSequence teamName) {
        return m_teamNamePattern.matcher(teamName).matches();
    }


    public void emptyTeams(boolean allTeams) {
        for (Team t : m_mainScoreboard.getTeams()) {
            Matcher matcher = m_teamNamePattern.matcher(t.getName());
            if (matcher.matches() || allTeams) {
                for (OfflinePlayer p : t.getPlayers()) {
                    t.removePlayer(p);
                    if (p.isOnline()) {
                        p.getPlayer().sendMessage(ChatColor.GOLD + "You were removed from your team");
                    }
                }
            }
        }
    }
}
