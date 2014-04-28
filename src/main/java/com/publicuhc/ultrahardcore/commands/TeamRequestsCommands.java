/*
 * TeamRequestsCommand.java
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
import com.publicuhc.pluginframework.commands.routing.Router;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.pluginframework.util.UUIDFetcher;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import com.publicuhc.ultrahardcore.util.TeamsUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class TeamRequestsCommands extends SimpleCommand {

    public static final String REQUEST_TEAM_PERMISSION = "UHC.teams.request.request";
    public static final String REQUEST_TEAM_REPLY_PERMISSION = "UHC.teams.request.reply";

    private final Map<String, List<String>> m_requests = new HashMap<String, List<String>>();
    private final TeamsUtil m_teamsUtil;

    /**
     * @param configManager the config manager
     * @param translate     the translator
     * @param router        the router
     * @param util          the TeamsUtil class
     */
    @Inject
    protected TeamRequestsCommands(Configurator configManager, Translate translate, Router router, TeamsUtil util) {
        super(configManager, translate);
        m_teamsUtil = util;

        List<String> syntax = new ArrayList<String>();
        syntax.add(ChatColor.GRAY + "/reqteam player1 player2 player3...");
        syntax.add(ChatColor.GRAY + "/reqteam accept|deny playername");
        syntax.add(ChatColor.GRAY + "/reqteam list");

        router.setDefaultMessageForCommand("reqteam", syntax);
    }

    @CommandMethod
    public void requestTeam(CommandRequest request) {
        List<String> args = request.getArgs();
        StringBuilder builder = new StringBuilder();
        for(String s  : args) {
            builder.append(s).append(" ");
        }

        m_requests.put(request.getSender().getName(), request.getArgs());

        Map<String, String> context = new HashMap<String, String>();
        context.put("name", request.getSender().getName());
        context.put("names", builder.toString());

        request.sendMessage(translate("teams.request.submitted", request.getLocale()));

        ServerUtil.broadcastForPermission(
                translate(
                        "teams.request.announce",
                        getTranslator().getLocaleForSender(Bukkit.getConsoleSender()),
                        context
                ),
                REQUEST_TEAM_REPLY_PERMISSION
        );
    }

    @RouteInfo
    public void requestTeamDetails(RouteBuilder builder) {
        builder.restrictCommand("reqteam");
        builder.restrictPermission(REQUEST_TEAM_PERMISSION);
        builder.restrictPattern(Pattern.compile(".+"));
        builder.restrictSenderType(SenderType.PLAYER);
        builder.maxMatches(1);
    }

    @CommandMethod
    public void requestTeamReplyAccept(CommandRequest request) {
        String toCheck = request.getArg(1);

        //get the requested team
        List<String> team = m_requests.get(toCheck);

        //if player hasn't requested anything
        if(null == team) {
            request.sendMessage(translate("teams.request.not_found", request.getLocale()));
            return;
        }

        //add the requester to the team list
        team.add(toCheck);

        Collection<String> notFound = new ArrayList<String>();
        Collection<Player> found = new ArrayList<Player>();

        //get the online players first
        for(int i = 0; i < team.size(); i++) {
            Player player = request.getPlayer(i);
            if(null == player) {
                notFound.add(request.getArg(i));
            }
            found.add(player);
        }

        //get the offline player's UUIDs
        Collection<UUID> uuids = new ArrayList<UUID>();

        if(!notFound.isEmpty()) {
            Map<String, UUID> fetched = new HashMap<String, UUID>();

            UUIDFetcher fetcher = new UUIDFetcher(notFound.toArray(new String[notFound.size()]));
            try {
                fetched = fetcher.call();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (String current : notFound) {
                UUID uu = fetched.get(current);
                if (null == uu) {
                    request.sendMessage(translate("teams.player_not_found", request.getLocale(), "name", current));
                    continue;
                }
                uuids.add(uu);
            }
        }

        //get the next available team
        Team t = m_teamsUtil.getNextAvailableTeam(false);

        //set up the context
        Map<String, String> context = new HashMap<String, String>();
        context.put("display", t.getDisplayName());
        context.put("name", t.getName());

        //for all the online players add them to the team and tell them
        for(Player p : found) {
            t.addPlayer(p);
            p.sendMessage(translate("teams.joined_notification", getTranslator().getLocaleForSender(p), context));
        }

        //for all the found offline players
        for(UUID uuid : uuids) {
            t.addPlayer(Bukkit.getOfflinePlayer(uuid));
        }

        //if the requester is still online tell them it was accepted
        Player p = Bukkit.getPlayer(toCheck);
        if(null != p) {
            p.sendMessage(translate("teams.request.requester_accepted", getTranslator().getLocaleForSender(p)));
        }

        //tell sender it was accepted
        request.sendMessage(translate("teams.created", request.getLocale(), context));
    }

    @RouteInfo
    public void requestTeamReplyAcceptDetails(RouteBuilder builder) {
        builder.restrictCommand("reqteam");
        builder.restrictPermission(REQUEST_TEAM_REPLY_PERMISSION);
        builder.restrictPattern(Pattern.compile("(accept) .+"));
    }

    @CommandMethod
    public void requestTeamReplyDeny(CommandRequest request) {
        String toCheck = request.getArg(1);

        List<String> team = m_requests.get(toCheck);
        if(null == team) {
            request.sendMessage(translate("teams.request.not_found", request.getLocale()));
            return;
        }

        m_requests.remove(toCheck);

        //if the requester is still online tell them it was denied
        Player p = Bukkit.getPlayer(toCheck);
        if(null != p) {
            p.sendMessage(translate("teams.request.requester_denied", getTranslator().getLocaleForSender(p)));
        }

        //feedback to sender
        request.sendMessage(translate("teams.request.denied", request.getLocale()));
    }

    @RouteInfo
    public void requestTeamReplyDenyDetails(RouteBuilder builder) {
        builder.restrictCommand("reqteam");
        builder.restrictPermission(REQUEST_TEAM_REPLY_PERMISSION);
        builder.restrictPattern(Pattern.compile("(deny) .+"));
    }

    @CommandMethod
    public void requestTeamList(CommandRequest request) {
        if(m_requests.isEmpty()) {
            request.sendMessage(translate("teams.request.open.none", request.getLocale()));
            return;
        }

        request.sendMessage(translate("teams.request.open.title", request.getLocale()));
        for(Map.Entry<String, List<String>> stringListEntry : m_requests.entrySet()) {
            Map<String, String> context = new HashMap<String, String>();
            context.put("name", stringListEntry.getKey());
            StringBuilder builder = new StringBuilder();
            for(String s : stringListEntry.getValue()) {
                builder.append(s).append(" ");
            }
            context.put("names", builder.toString());
            request.sendMessage(translate("teams.request.open.request", request.getLocale(), context));
        }
    }

    @RouteInfo
    public void requestTeamListDetails(RouteBuilder builder) {
        builder.restrictCommand("reqteam");
        builder.restrictPermission(REQUEST_TEAM_REPLY_PERMISSION);
        builder.restrictPattern(Pattern.compile("list"));
    }
}
