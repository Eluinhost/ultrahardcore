package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.commands.SenderType;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.util.MathsHelper;
import com.publicuhc.ultrahardcore.util.TeamsUtil;
import com.publicuhc.ultrahardcore.util.WordsUtil;

import java.util.*;

public class TeamCommands extends SimpleCommand {

    private final TeamsUtil m_teamsUtil;

    public static final String TEAM_CREATE_PERMISSION = "UHC.teams.create";
    public static final String TEAM_REMOVE_PERMISSION = "UHC.teams.remove";
    public static final String TEAM_LEAVE_PERMISSION = "UHC.teams.leave";
    public static final String TEAM_LEAVE_OTHER_PERMISSION = "UHC.teams.leave.other";
    public static final String TEAM_JOIN_PERMISSION = "UHC.teams.join";
    public static final String TEAM_JOIN_OTHER_PERMISSION = "UHC.teams.join.other";
    public static final String CLEAR_TEAMS_PERMISSION = "UHC.teams.clear";
    public static final String EMPTY_TEAMS_PERMISSION = "UHC.teams.empty";
    public static final String LIST_TEAMS_PERMISSION = "UHC.teams.list";
    public static final String RANDOM_TEAMS_PERMISSION = "UHC.teams.random";

    private final WordsUtil m_words;

    @Inject
    private TeamCommands(ConfigManager configManager, WordsUtil words, TeamsUtil teamsUtil) {
        super(configManager);
        m_words = words;
        m_teamsUtil = teamsUtil;
    }

    /**
     * Ran on /createteam [name]
     * @param request request params
     */
    @Command(trigger = "createteam",
            identifier = "CreateTeamCommand",
            minArgs = 0,
            maxArgs = 1,
            permission = TEAM_CREATE_PERMISSION)
    public void onCreateTeamCommand(CommandRequest request){
        Team thisteam;
        if (request.getArgs().size() == 1) {
            thisteam = m_teamsUtil.getTeam(request.getFirstArg());
            if (thisteam != null) {
                request.sendMessage(translate("teams.already_exists").replaceAll("%name%",thisteam.getName()));
                return;
            }
            thisteam = m_teamsUtil.registerNewTeam(request.getFirstArg());
            thisteam.setDisplayName(m_words.getRandomTeamName());
        } else {
            thisteam = m_teamsUtil.getNextAvailableTeam(true);
        }
        request.sendMessage(translate("teams.created").replaceAll("%display%",thisteam.getDisplayName()).replaceAll("%name%",thisteam.getName()));
    }

    /**
     * Ran on /removeteam {name}
     * @param request request params
     */
    @Command(trigger = "removeteam",
            identifier = "RemoveTeamCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = TEAM_REMOVE_PERMISSION)
    public void onRemoveTeamCommand(CommandRequest request){
        Team team = m_teamsUtil.getTeam(request.getFirstArg());
        if (team == null) {
            request.sendMessage(translate("teams.not_exist"));
            return;
        }
        for (OfflinePlayer p : team.getPlayers()) {
            Player p1 = p.getPlayer();
            if (p1 != null) {
                p1.sendMessage(translate("teams.disbanded"));
            }
        }
        m_teamsUtil.removeTeam(request.getFirstArg());
        request.sendMessage(translate("teams.removed"));
    }

    /**
     * Ran on /leaveteam
     * @param request request params
     */
    @Command(trigger = "leaveteam",
            identifier = "LeaveTeamCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = TEAM_LEAVE_PERMISSION,
            senders = {SenderType.PLAYER})
    public void onLeaveTeamCommand(CommandRequest request){
        boolean stillOnTeam = !m_teamsUtil.removePlayerFromTeam((OfflinePlayer) request.getSender(), true, true);
        if (stillOnTeam) {
            request.sendMessage(translate("teams.not_in_team"));
        }
    }

    /**
     * Ran on /leaveteam f {name}
     * @param request request params
     */
    @Command(trigger = "f",
            identifier = "LeaveTeamOtherCommand",
            parentID = "LeaveTeamCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = TEAM_LEAVE_OTHER_PERMISSION)
    public void onLeaveTeamForce(CommandRequest request){
        boolean stillOnTeam = !m_teamsUtil.removePlayerFromTeam(Bukkit.getOfflinePlayer(request.getFirstArg()), true, true);
        if (stillOnTeam) {
            request.getSender().sendMessage(translate("teams.player_not_in_team").replaceAll("%name%",request.getFirstArg()));
        }
    }

    /**
     * Ran on /jointeam {name}
     * @param request request params
     */
    @Command(trigger = "jointeam",
            identifier = "JoinTeamCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = TEAM_JOIN_PERMISSION,
            senders = {SenderType.PLAYER})
    public void onJoinTeamCommand(CommandRequest request){
        OfflinePlayer sender = (OfflinePlayer) request.getSender();
        Team team = m_teamsUtil.getTeam(request.getFirstArg());
        if (team == null) {
            request.sendMessage(translate("teams.not_exist"));
            return;
        }
        if(m_teamsUtil.getPlayersTeam(sender) != null){
            m_teamsUtil.removePlayerFromTeam(sender,true,true);
        }
        TeamsUtil.playerJoinTeam(sender, team, true, true);
    }

    /**
     * Ran on /jointeam f {team} {name}
     * @param request request params
     */
    @Command(trigger = "f",
            identifier = "JoinTeamOtherCommand",
            parentID = "JoinTeamCommand",
            minArgs = 2,
            maxArgs = 2,
            permission = TEAM_JOIN_OTHER_PERMISSION)
    public void onJoinTeamOtherCommand(CommandRequest request){
        Team team = m_teamsUtil.getTeam(request.getFirstArg());
        if (team == null) {
            request.sendMessage(translate("teams.not_exist"));
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(request.getLastArg());
        if(m_teamsUtil.getPlayersTeam(player) != null){
            m_teamsUtil.removePlayerFromTeam(player,true,true);
        }
        TeamsUtil.playerJoinTeam(player,team,true,true);
    }

    /**
     * Ran on /clearteams
     * @param request request params
     */
    @Command(trigger = "clearteams",
            identifier = "ClearTeamCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = CLEAR_TEAMS_PERMISSION)
    public void onClearTeamsCommand(CommandRequest request){
        m_teamsUtil.clearTeams(true);
        request.sendMessage(translate("teams.cleared"));
    }

    /**
     * Ran on /emptyteams
     * @param request request params
     */
    @Command(trigger = "emptyteams",
            identifier = "EmptyTeamCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = EMPTY_TEAMS_PERMISSION)
    public void onEmptyTeamsCommand(CommandRequest request){
        m_teamsUtil.emptyTeams(true);
        request.sendMessage(translate("teams.emptied"));
    }

    /**
     * Ran on /listteams {name}
     * @param request request params
     */
    @Command(trigger = "listteams",
            identifier = "ListTeamCommand",
            minArgs = 0,
            maxArgs = 1,
            permission = LIST_TEAMS_PERMISSION)
    public void onListTeamsCommand(CommandRequest request){
        if(request.getArgs().isEmpty()){
            onListTeamsAllCommand(request);
            return;
        }
        Team team = m_teamsUtil.getTeam(request.getFirstArg());
        if(team == null){
            request.sendMessage(translate("teams.not_exist"));
            return;
        }
        request.sendMessage(TeamsUtil.teamToString(team));
    }

    /**
     * Ran on /listteam *
     * @param request request params
     */
    @Command(trigger = "*",
            identifier = "ListTeamAllCommand",
            parentID = "ListTeamCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = LIST_TEAMS_PERMISSION)
    public void onListTeamsAllCommand(CommandRequest request){
        Set<Team> teams = m_teamsUtil.getAllTeams();
        if(teams.isEmpty()){
            request.sendMessage(translate("teams.no_teams"));
            return;
        }
        for(Team team : teams){
            request.sendMessage(TeamsUtil.teamToString(team));
        }
    }

    /**
     * Ran on /randomteams {number} [world]
     * @param request request params
     */
    @Command(trigger = "randomteams",
            identifier = "RandomTeamCommand",
            minArgs = 1,
            maxArgs = 2,
            permission = RANDOM_TEAMS_PERMISSION)
    public void onRandomTeamCommand(CommandRequest request){
        List<Player> players = new ArrayList<Player>();
        if(request.getArgs().size() == 2){
            World world = Bukkit.getWorld(request.getLastArg());
            if(world == null){
                request.sendMessage(translate("teams.invalid_world"));
                return;
            }
            players.addAll(world.getPlayers());
        }else{
            Collections.addAll(players, Bukkit.getOnlinePlayers());
        }
        Collections.shuffle(players);
        m_teamsUtil.removePlayersInATeam(players);

        int teamsToMake;
        try {
            teamsToMake = Integer.parseInt(request.getFirstArg());
        } catch (NumberFormatException ignored) {
            request.sendMessage(translate("teams.invalid_number_teams"));
            return;
        }

        if (teamsToMake > players.size()) {
            request.sendMessage(translate("teams.too_many_teams"));
            return;
        }

        if (teamsToMake <= 0) {
            request.sendMessage("teams.greater_zero");
            return;
        }

        List<List<Player>> teams = MathsHelper.split(players, teamsToMake);
        Collection<Team> finalTeams = new ArrayList<Team>();
        for (List<Player> team : teams) {
            if (team == null) {
                continue;
            }
            Team thisteam = m_teamsUtil.getNextAvailableTeam(false);
            finalTeams.add(thisteam);
            for (Player p : team) {
                if (p != null) {
                    TeamsUtil.playerJoinTeam(p, thisteam, true, false);
                }
            }
        }
        for (Team t : finalTeams) {
            Set<OfflinePlayer> teamPlayers = t.getPlayers();
            //TODO translatable
            StringBuilder buffer = new StringBuilder(String.valueOf(ChatColor.GOLD))
                    .append("Your Team (")
                    .append(t.getName())
                    .append(" - ")
                    .append(t.getDisplayName())
                    .append("): ");
            for (OfflinePlayer p : teamPlayers) {
                buffer.append(p.getName()).append(", ");
            }
            buffer.delete(buffer.length() - 2, buffer.length());
            String finalString = buffer.toString();
            for (OfflinePlayer p : teamPlayers) {
                if(p.isOnline()){
                    Player p2 = Bukkit.getPlayerExact(p.getName());
                    p2.sendMessage(finalString);
                }
            }
        }
        request.sendMessage(translate("teams.created_teams").replaceAll("%amount%", String.valueOf(teamsToMake)));
    }
}
