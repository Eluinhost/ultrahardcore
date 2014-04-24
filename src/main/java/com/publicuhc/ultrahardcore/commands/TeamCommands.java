/*
 * TeamCommands.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routing.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.MathsHelper;
import com.publicuhc.ultrahardcore.util.TeamsUtil;
import com.publicuhc.ultrahardcore.util.WordsUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.regex.Pattern;

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
    private TeamCommands(Configurator configManager, Translate translate, WordsUtil words, TeamsUtil teamsUtil) {
        super(configManager, translate);
        m_words = words;
        m_teamsUtil = teamsUtil;
    }

    /**
     * Ran on /createteam [name]
     * @param request request params
     */
    @CommandMethod
    public void createTeamCommand(CommandRequest request){
        Team thisteam;
        if (request.getArgs().size() == 1) {
            thisteam = m_teamsUtil.getTeam(request.getFirstArg());
            if (thisteam != null) {
                request.sendMessage(translate("teams.already_exists", request.getLocale(), "name", thisteam.getName()));
                return;
            }
            thisteam = m_teamsUtil.registerNewTeam(request.getFirstArg());
            thisteam.setDisplayName(m_words.getRandomTeamName());
        } else {
            thisteam = m_teamsUtil.getNextAvailableTeam(true);
        }
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("display", thisteam.getDisplayName());
        vars.put("name", thisteam.getName());
        request.sendMessage(translate("teams.created", request.getLocale(), vars));
    }

    @RouteInfo
    public void createTeamCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("createteam");
        builder.restrictPermission(TEAM_CREATE_PERMISSION);
    }

    /**
     * Ran on /removeteam {name}
     * @param request request params
     */
    @CommandMethod
    public void removeTeamCommand(CommandRequest request){
        Team team = m_teamsUtil.getTeam(request.getFirstArg());
        if (team == null) {
            request.sendMessage(translate("teams.not_exist", request.getLocale()));
            return;
        }
        for (OfflinePlayer p : team.getPlayers()) {
            Player p1 = p.getPlayer();
            if (p1 != null) {
                p1.sendMessage(translate("teams.disbanded", request.getLocale()));
            }
        }
        m_teamsUtil.removeTeam(request.getFirstArg());
        request.sendMessage(translate("teams.removed", request.getLocale()));
    }

    @RouteInfo
    public void removeTeamCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("removeteam");
        builder.restrictPermission(TEAM_REMOVE_PERMISSION);
        builder.restrictPattern(Pattern.compile("[\\S]+"));
    }

    /**
     * Ran on /leaveteam
     * @param request request params
     */
    @CommandMethod
    public void leaveTeamCommand(CommandRequest request){
        boolean stillOnTeam = !m_teamsUtil.removePlayerFromTeam((OfflinePlayer) request.getSender(), true, true);
        if (stillOnTeam) {
            request.sendMessage(translate("teams.not_in_team", request.getLocale()));
        }
    }

    @RouteInfo
    public void leaveTeamCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("leaveteam");
        builder.restrictPermission(TEAM_LEAVE_PERMISSION);
        builder.restrictSenderType(SenderType.PLAYER);
    }

    /**
     * Ran on /leaveteam f {name}
     * @param request request params
     */
    @CommandMethod
    public void leaveTeamForce(CommandRequest request) {
        UUID playerID = request.getPlayerUUID(1);
        if(playerID.equals(CommandRequest.INVALID_ID)) {
            request.sendMessage(translate("teams.invalid_player", request.getLocale()));
        }
        boolean stillOnTeam = !m_teamsUtil.removePlayerFromTeam(Bukkit.getOfflinePlayer(playerID), true, true);
        if (stillOnTeam) {
            request.getSender().sendMessage(translate("teams.player_not_in_team", request.getLocale() , "name", request.getFirstArg()));
        }
    }

    @RouteInfo
    public void leaveTeamForceDetails(RouteBuilder builder) {
        builder.restrictCommand("leaveteam");
        builder.restrictPermission(TEAM_LEAVE_OTHER_PERMISSION);
        builder.restrictPattern(Pattern.compile("f [\\S]+"));
    }

    /**
     * Ran on /jointeam {name}
     * @param request request params
     */
    @CommandMethod
    public void joinTeamCommand(CommandRequest request){
        OfflinePlayer sender = (OfflinePlayer) request.getSender();
        Team team = m_teamsUtil.getTeam(request.getFirstArg());
        if (team == null) {
            request.sendMessage(translate("teams.not_exist", request.getLocale()));
            return;
        }
        if(m_teamsUtil.getPlayersTeam(sender) != null){
            m_teamsUtil.removePlayerFromTeam(sender,true,true);
        }
        TeamsUtil.playerJoinTeam(sender, team, true, true);
    }

    @RouteInfo
    public void joinTeamCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("jointeam");
        builder.restrictPattern(Pattern.compile("[\\S]+"));
        builder.restrictPermission(TEAM_JOIN_PERMISSION);
        builder.restrictSenderType(SenderType.PLAYER);
    }

    /**
     * Ran on /jointeam f {team} {name}
     * @param request request params
     */
    @CommandMethod
    public void joinTeamOtherCommand(CommandRequest request){
        Team team = m_teamsUtil.getTeam(request.getFirstArg());
        if (team == null) {
            request.sendMessage(translate("teams.not_exist", request.getLocale()));
            return;
        }
        UUID playerID = request.getPlayerUUID(2);
        if(playerID.equals(CommandRequest.INVALID_ID)) {
            request.sendMessage(translate("teams.invalid_player", request.getLocale()));
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);
        if(m_teamsUtil.getPlayersTeam(player) != null){
            m_teamsUtil.removePlayerFromTeam(player,true,true);
        }
        TeamsUtil.playerJoinTeam(player,team,true,true);
    }

    @RouteInfo
    public void joinTeamOtherCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("jointeam");
        builder.restrictPermission(TEAM_JOIN_OTHER_PERMISSION);
        builder.restrictPattern(Pattern.compile("f [\\S]+ [\\S]+"));
    }

    /**
     * Ran on /clearteams
     * @param request request params
     */
    @CommandMethod
    public void clearTeamsCommand(CommandRequest request) {
        m_teamsUtil.clearTeams(true);
        request.sendMessage(translate("teams.cleared", request.getLocale()));
    }

    @RouteInfo
    public void clearTeamsCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(CLEAR_TEAMS_PERMISSION);
        builder.restrictCommand("clearteams");
    }

    /**
     * Ran on /emptyteams
     * @param request request params
     */
    @CommandMethod
    public void emptyTeamsCommand(CommandRequest request){
        m_teamsUtil.emptyTeams(true);
        request.sendMessage(translate("teams.emptied", request.getLocale()));
    }

    @RouteInfo
    public void emptyTeamsCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("emptyteams");
        builder.restrictPermission(EMPTY_TEAMS_PERMISSION);
    }

    /**
     * Ran on /listteams {name}
     * @param request request params
     */
    @CommandMethod
    public void listTeamsCommand(CommandRequest request){
        if(request.getArgs().isEmpty()){
            listTeamsAllCommand(request);
            return;
        }
        Team team = m_teamsUtil.getTeam(request.getFirstArg());
        if(team == null){
            request.sendMessage(translate("teams.not_exist", request.getLocale()));
            return;
        }
        request.sendMessage(TeamsUtil.teamToString(team));
    }

    @RouteInfo
    public void listTeamsCommand(RouteBuilder builder) {
        builder.restrictCommand("listteams");
        builder.restrictPermission(LIST_TEAMS_PERMISSION);
    }

    /**
     * Ran on /listteam *
     * @param request request params
     */
    @CommandMethod
    public void listTeamsAllCommand(CommandRequest request){
        Set<Team> teams = m_teamsUtil.getAllTeams();
        if(teams.isEmpty()){
            request.sendMessage(translate("teams.no_teams", request.getLocale()));
            return;
        }
        for(Team team : teams){
            request.sendMessage(TeamsUtil.teamToString(team));
        }
    }

    @RouteInfo
    public void listTeamsAllCommand(RouteBuilder builder) {
        builder.restrictCommand("listteams");
        builder.restrictPermission(LIST_TEAMS_PERMISSION);
        builder.restrictPattern(Pattern.compile("\\*"));
    }

    /**
     * Ran on /randomteams {number} [world]
     * @param request request params
     */
    @CommandMethod
    public void randomTeamCommand(CommandRequest request){
        List<Player> players = new ArrayList<Player>();
        if(request.getArgs().size() == 2){
            World world = Bukkit.getWorld(request.getLastArg());
            if(world == null){
                request.sendMessage(translate("teams.invalid_world", request.getLocale()));
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
            request.sendMessage(translate("teams.invalid_number_teams", request.getLocale()));
            return;
        }

        if (teamsToMake > players.size()) {
            request.sendMessage(translate("teams.too_many_teams", request.getLocale()));
            return;
        }

        if (teamsToMake <= 0) {
            request.sendMessage(translate("teams.greater_zero", request.getLocale()));
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
                    Player p2 = Bukkit.getPlayer(p.getUniqueId());
                    p2.sendMessage(finalString);
                }
            }
        }
        request.sendMessage(translate("teams.created_teams", request.getLocale(), "amount", String.valueOf(teamsToMake)));
    }

    @RouteInfo
    public void randomTeamCommand(RouteBuilder builder) {
        builder.restrictCommand("randomteams");
        builder.restrictPattern(Pattern.compile("[\\S]+.*"));
        builder.restrictPermission(RANDOM_TEAMS_PERMISSION);
    }
}
