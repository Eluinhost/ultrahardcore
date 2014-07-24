/*
 * WhitelistCommandsTeset.java
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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.*;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class WhitelistCommandsTest {

    private WhitelistCommands whitelist;
    private Translate translate;

    private Player ghowden;
    private Player eluinhost;
    private Player fuzzboxx;
    private Player sonmica;

    @Before
    public void onStartup() {
        Injector injector = Guice.createInjector(
            new ConfigurationModule(getClass().getClassLoader()),
            new TranslateModule(),
            new AbstractModule() {
                @Override
                protected void configure() {
                    bind(File.class).annotatedWith(Names.named("dataFolder")).toInstance(new File("target" + File.separator + "testdatafolder"));
                }
            }
        );
        whitelist = injector.getInstance(WhitelistCommands.class);
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

        Set<OfflinePlayer> whitelist = new HashSet<OfflinePlayer>();
        whitelist.add(ghowden);
        whitelist.add(fuzzboxx);
        whitelist.add(eluinhost);

        when(Bukkit.getWhitelistedPlayers()).thenReturn(whitelist);
    }

    @Test
    public void testWhitelistAllCommand() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("whitelistall");

        Player p = mock(Player.class);

        builder.setArguments(new ArrayList<String>())
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        whitelist.whitelistAllCommand(builder.build());

        verify(p).sendMessage(translate.translate("whitelist.added", "en"));

        verify(ghowden, times(1)).setWhitelisted(true);
        verify(fuzzboxx, times(1)).setWhitelisted(true);
        verify(eluinhost, times(1)).setWhitelisted(true);
        verify(sonmica, never()).setWhitelisted(anyBoolean());
    }

    @Test
    public void testWhitelistClearCommand() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("whitelistall");

        Player p = mock(Player.class);

        List<String> args = new ArrayList<String>();
        args.add("clear");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        whitelist.whitelistClearCommand(builder.build());

        verify(ghowden, times(1)).setWhitelisted(false);
        verify(fuzzboxx, times(1)).setWhitelisted(false);
        verify(eluinhost, times(1)).setWhitelisted(false);
        verify(sonmica, never()).setWhitelisted(anyBoolean());

        verify(p).sendMessage(translate.translate("whitelist.cleared", "en"));
    }
}
