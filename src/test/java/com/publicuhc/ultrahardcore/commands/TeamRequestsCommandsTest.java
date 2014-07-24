/*
 * TeamRequestsCommandsTest.java
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

import com.publicuhc.pluginframework.PluginModule;
import com.publicuhc.pluginframework.commands.CommandModule;
import com.publicuhc.pluginframework.commands.requests.CommandRequestBuilder;
import com.publicuhc.pluginframework.commands.requests.DefaultCommandRequestBuilder;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.shaded.inject.Guice;
import com.publicuhc.pluginframework.shaded.inject.Injector;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.pluginframework.translate.TranslateModule;
import com.publicuhc.ultrahardcore.UHCModule;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class TeamRequestsCommandsTest {

    private TeamRequestsCommands requests;
    private Translate translate;

    private Team uhc0;

    private Player ghowden;
    private Player eluinhost;
    private Player fuzzboxx;
    private Player sonmica;

    private final UUID sonmicaUUID = UUID.fromString("0123456789ab-0123-0123-0123-01234567");

    @Before
    public void onStartup() throws Exception
    {
        mockStatic(Bukkit.class);
        ScoreboardManager manager = mock(ScoreboardManager.class);
        when(Bukkit.getScoreboardManager()).thenReturn(manager);
        Scoreboard scoreboard = mock(Scoreboard.class);
        when(manager.getMainScoreboard()).thenReturn(scoreboard);

        uhc0 = mock(Team.class);
        when(uhc0.getSize()).thenReturn(0);
        when(uhc0.getName()).thenReturn("UHC0");
        when(uhc0.getDisplayName()).thenReturn("TEAMNAME");
        when(scoreboard.getTeam("UHC0")).thenReturn(uhc0);

        PluginDescriptionFile pdf = new PluginDescriptionFile("test", "0.1", "com.publicuhc.pluginframework.FrameworkJavaPlugin");
        Plugin plugin = mock(Plugin.class);
        when(plugin.getDescription()).thenReturn(pdf);
        when(plugin.getDataFolder()).thenReturn(new File("target"+ File.separator+"testdatafolder"));

        Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(Logger.getAnonymousLogger());
        when(plugin.getServer()).thenReturn(server);

        Injector injector = Guice.createInjector(
                new ConfigurationModule(getClass().getClassLoader()),
                new TranslateModule(),
                new CommandModule(),
                new PluginModule(plugin),
                new UHCModule()
        );
        requests = injector.getInstance(TeamRequestsCommands.class);
        translate = injector.getInstance(Translate.class);

        ghowden = mock(Player.class);
        eluinhost = mock(Player.class);
        fuzzboxx = mock(Player.class);
        sonmica = mock(Player.class);

        when(Bukkit.getPlayer("ghowden")).thenReturn(ghowden);
        when(Bukkit.getPlayer("eluinhost")).thenReturn(eluinhost);
        when(Bukkit.getPlayer("fuzzboxx")).thenReturn(fuzzboxx);
        when(Bukkit.getPlayer("sonmica")).thenReturn(null);

        List<Player> onlinePlayers = new ArrayList<Player>();
        onlinePlayers.add(ghowden);
        onlinePlayers.add(eluinhost);
        onlinePlayers.add(fuzzboxx);
        PowerMockito.<List<? extends Player>>when(Bukkit.getOnlinePlayers()).thenReturn(onlinePlayers);

        when(ghowden.hasPermission(TeamRequestsCommands.REQUEST_TEAM_REPLY_PERMISSION)).thenReturn(true);
        when(eluinhost.hasPermission(TeamRequestsCommands.REQUEST_TEAM_REPLY_PERMISSION)).thenReturn(true);
        when(fuzzboxx.hasPermission(TeamRequestsCommands.REQUEST_TEAM_REPLY_PERMISSION)).thenReturn(false);

        when(Bukkit.getPlayer(sonmicaUUID)).thenReturn(sonmica);
    }

    @Test
    public void testRequestTeam() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("reqteam");

        Player p = mock(Player.class);
        when(p.getName()).thenReturn("testplayer");

        List<String> args = new ArrayList<String>();
        args.add("ghowden");
        args.add("eluinhost");
        args.add("fuzzboxx");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        requests.requestTeam(builder.build());

        Map<String, String> context = new HashMap<String, String>();
        context.put("name", "testplayer");
        context.put("names", "ghowden eluinhost fuzzboxx ");

        verify(p).sendMessage(translate.translate("teams.request.submitted", "en", context));

        verify(ghowden, times(1)).sendMessage(translate.translate(
                "teams.request.announce",
                "en",
                context
        ));
        verify(eluinhost, times(1)).sendMessage(translate.translate(
                "teams.request.announce",
                "en",
                context
        ));
        verify(fuzzboxx, never()).sendMessage(anyString());
        verify(sonmica, never()).sendMessage(anyString());

        assertThat(requests.getRequests().get("testplayer")).isNotNull();
        assertThat(requests.getRequests().get("testplayer")).contains("ghowden", "eluinhost", "fuzzboxx");
    }

    @Test
    public void testRequestDeny() {
        requests.addRequest("ghowden", Arrays.asList("ghowden", "eluinhost", "fuzzboxx"));

        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("reqteam");

        Player p = mock(Player.class);
        when(p.getName()).thenReturn("testplayer");

        List<String> args = new ArrayList<String>();
        args.add("deny");
        args.add("invalidplayer");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        requests.requestTeamReplyDeny(builder.build());

        verify(p).sendMessage(translate.translate("teams.request.not_found", "en"));

        args = new ArrayList<String>();
        args.add("deny");
        args.add("ghowden");

        builder.setArguments(args);

        requests.requestTeamReplyDeny(builder.build());

        assertThat(requests.getRequests().size()).isEqualTo(0);

        verify(p, times(1)).sendMessage(translate.translate("teams.request.denied", "en"));
        verify(ghowden, times(1)).sendMessage(translate.translate("teams.request.requester_denied", "en"));
        verify(eluinhost, never()).sendMessage(anyString());
    }

    @Test
    public void testRequestList() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("reqteam");

        Player p = mock(Player.class);

        List<String> args = new ArrayList<String>();
        args.add("list");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        requests.requestTeamList(builder.build());

        verify(p, times(1)).sendMessage(translate.translate("teams.request.open.none", "en"));

        requests.addRequest("ghowden", Arrays.asList("ghowden", "fuzzboxx", "eluinhost"));

        requests.requestTeamList(builder.build());

        Map<String, String> context = new HashMap<String, String>();
        context.put("name", "ghowden");
        context.put("names", "ghowden fuzzboxx eluinhost ");

        verify(p, times(1)).sendMessage(translate.translate("teams.request.open.title", "en"));
        verify(p, times(1)).sendMessage(translate.translate("teams.request.open.request", "en", context));
    }

    @Test
    public void testRequestAccept() {
        List<String> teamreq = new ArrayList<String>();
        teamreq.add("ghowden");
        teamreq.add("fuzzboxx");
        teamreq.add("eluinhost");
        requests.addRequest("ghowden", teamreq);

        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("reqteam");

        Player p = mock(Player.class);

        List<String> args = new ArrayList<String>();
        args.add("accept");
        args.add("invalidplayer");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        requests.requestTeamReplyAccept(builder.build());

        assertThat(requests.getRequests().size()).isEqualTo(1);
        verify(p, times(1)).sendMessage(translate.translate("teams.request.not_found", "en"));
        assertThat(requests.getRequests().size()).isEqualTo(1);

        args = new ArrayList<String>();
        args.add("accept");
        args.add("ghowden");

        builder.setArguments(args);

        requests.requestTeamReplyAccept(builder.build());

        assertThat(requests.getRequests().size()).isEqualTo(0);

        Map<String, String> context = new HashMap<String, String>();
        context.put("display", "TEAMNAME");
        context.put("name", "UHC0");

        verify(p, times(1)).sendMessage(translate.translate("teams.created", "en", context));
        verify(ghowden, times(1)).sendMessage(translate.translate("teams.request.requester_accepted", "en"));
        verify(eluinhost, times(1)).sendMessage(translate.translate("teams.joined_notification", "en", context));

        verify(uhc0, times(2)).addPlayer(ghowden);
        verify(uhc0, times(1)).addPlayer(eluinhost);
        verify(uhc0, times(1)).addPlayer(fuzzboxx);
        verify(uhc0, never()).addPlayer(sonmica);
    }
}
