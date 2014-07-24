/*
 * TPCommandTest.java
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

import com.publicuhc.pluginframework.commands.requests.CommandRequestBuilder;
import com.publicuhc.pluginframework.commands.requests.DefaultCommandRequestBuilder;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.Guice;
import com.publicuhc.pluginframework.shaded.inject.Injector;
import com.publicuhc.pluginframework.shaded.inject.name.Names;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.pluginframework.translate.TranslateModule;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class TPCommandTest {

    private TPCommand teleport;
    private Translate translate;

    private Player ghowden;
    private Player eluinhost;
    private Player fuzzboxx;
    private Player sonmica;

    private Location tpLocation;
    private World world;

    private ScatterManager scatterManager;

    @Before
    public void onStartUp() {
        scatterManager = mock(ScatterManager.class);
        Injector injector = Guice.createInjector(
                new ConfigurationModule(getClass().getClassLoader()),
                new TranslateModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(File.class).annotatedWith(Names.named("dataFolder")).toInstance(new File("target" + File.separator + "testdatafolder"));
                        bind(ScatterManager.class).toInstance(scatterManager);
                    }
                }
        );
        teleport = injector.getInstance(TPCommand.class);
        translate = injector.getInstance(Translate.class);

        mockStatic(Bukkit.class);

        ghowden = mock(Player.class);
        eluinhost = mock(Player.class);
        fuzzboxx = mock(Player.class);
        sonmica = mock(Player.class);

        Collection<Player> onlinePlayers = new ArrayList<Player>();
        onlinePlayers.add(ghowden);
        onlinePlayers.add(eluinhost);
        onlinePlayers.add(fuzzboxx);
        doReturn(onlinePlayers).when(Bukkit.getOnlinePlayers());

        world = mock(World.class);
        when(Bukkit.getWorld("validworld")).thenReturn(world);
        when(Bukkit.getWorld("invalidworld")).thenReturn(null);

        when(Bukkit.getPlayer("ghowden")).thenReturn(ghowden);
        when(Bukkit.getPlayer("eluinhost")).thenReturn(eluinhost);
        when(Bukkit.getPlayer("fuzzboxx")).thenReturn(fuzzboxx);
        when(Bukkit.getPlayer("sonmica")).thenReturn(null);

        tpLocation = new Location(world, -100, 200, 0);
    }

    @Test
    public void testTeleportLocationWorld() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("tpp");

        Player p = mock(Player.class);

        List<String> args = new ArrayList<String>();
        args.add("ghowden");
        args.add("eluinhost");
        args.add("-100,200,0,validworld");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        teleport.teleportCommand(builder.build());

        verify(p).sendMessage(translate.translate("teleport.all_teleported", "en"));
        verify(scatterManager, times(2)).teleportSafe(any(Player.class), eq(tpLocation), eq(false));
    }

    @Test
    public void testTeleportLocationInvalidWorld() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("tpp");

        Player p = mock(Player.class);

        List<String> args = new ArrayList<String>();
        args.add("ghowden");
        args.add("eluinhost");
        args.add("-100,200,0,invalidworld");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        teleport.teleportCommand(builder.build());

        verify(p).sendMessage(translate.translate("teleport.invalid.world", "en"));

        verify(scatterManager, never()).teleportSafe(any(Player.class), eq(tpLocation), eq(false));
    }

    @Test
    public void testTeleportCommandLocationNoProvidedWorldPlayer() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("tpp");

        Player p = mock(Player.class);

        when(p.getWorld()).thenReturn(world);

        List<String> args = new ArrayList<String>();
        args.add("ghowden");
        args.add("eluinhost");
        args.add("-100,200,0");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        teleport.teleportCommand(builder.build());

        verify(p).sendMessage(translate.translate("teleport.all_teleported", "en"));

        verify(scatterManager, times(2)).teleportSafe(any(Player.class), eq(tpLocation), eq(false));
    }

    @Test
    public void testTeleportCommandLocationNoProvidedWorldConsole() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("tpp");

        ConsoleCommandSender p = mock(ConsoleCommandSender.class);

        List<String> args = new ArrayList<String>();
        args.add("ghowden");
        args.add("eluinhost");
        args.add("-100,200,0");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        teleport.teleportCommand(builder.build());

        verify(p).sendMessage(translate.translate("teleport.non_player_world", "en"));

        verify(scatterManager, never()).teleportSafe(any(Player.class), eq(tpLocation), eq(false));
    }

    @Test
    public void testTeleportCommandToPlayer() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("tpp");

        Player p = mock(Player.class);

        when(fuzzboxx.getLocation()).thenReturn(tpLocation);

        List<String> args = new ArrayList<String>();
        args.add("ghowden");
        args.add("eluinhost");
        args.add("fuzzboxx");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        teleport.teleportCommand(builder.build());

        verify(p).sendMessage(translate.translate("teleport.all_teleported", "en"));

        verify(scatterManager, times(2)).teleportSafe(any(Player.class), eq(tpLocation), eq(false));
    }

    @Test
    public void testTeleportCommandToInvalidPlayer() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("tpp");

        Player p = mock(Player.class);

        when(fuzzboxx.getLocation()).thenReturn(tpLocation);

        List<String> args = new ArrayList<String>();
        args.add("ghowden");
        args.add("eluinhost");
        args.add("sonmica");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        teleport.teleportCommand(builder.build());

        verify(p).sendMessage(translate.translate("teleport.invalid.player", "en", "name", "sonmica"));

        verify(scatterManager, never()).teleportSafe(any(Player.class), eq(tpLocation), eq(false));
    }

    @Test
    public void testTeleportCommandAll() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("tpp");

        Player p = mock(Player.class);

        when(fuzzboxx.getLocation()).thenReturn(tpLocation);

        List<String> args = new ArrayList<String>();
        args.add("*");
        args.add("fuzzboxx");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        teleport.teleportCommand(builder.build());

        verify(p).sendMessage(translate.translate("teleport.all_teleported", "en"));

        verify(scatterManager, times(3)).teleportSafe(any(Player.class), eq(tpLocation), eq(false));
    }
}
