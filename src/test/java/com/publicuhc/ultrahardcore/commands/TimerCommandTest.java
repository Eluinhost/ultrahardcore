/*
 * TimerCommandTest.java
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
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.Guice;
import com.publicuhc.pluginframework.shaded.inject.Injector;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.pluginframework.translate.TranslateModule;
import com.publicuhc.ultrahardcore.borders.BorderTypeManager;
import com.publicuhc.ultrahardcore.borders.RealBorderTypeManager;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.pluginfeatures.timer.TimerFeature;
import com.publicuhc.ultrahardcore.scatter.FallProtector;
import com.publicuhc.ultrahardcore.scatter.Protector;
import com.publicuhc.ultrahardcore.scatter.RealScatterManager;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class TimerCommandTest {

    private TimerCommand timer;

    private FeatureManager features;
    private Translate translate;

    @Before
    public void onStartUp() {
        PluginDescriptionFile pdf = new PluginDescriptionFile("test", "0.1", "com.publicuhc.pluginframework.FrameworkJavaPlugin");
        Plugin plugin = mock(Plugin.class);
        when(plugin.getDescription()).thenReturn(pdf);
        when(plugin.getDataFolder()).thenReturn(new File("target"+ File.separator+"testdatafolder"));

        Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(Logger.getAnonymousLogger());
        when(plugin.getServer()).thenReturn(server);

        features = mock(FeatureManager.class);

        Injector injector = Guice.createInjector(
                new ConfigurationModule(getClass().getClassLoader()),
                new TranslateModule(),
                new CommandModule(),
                new PluginModule(plugin),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(FeatureManager.class).toInstance(features);
                        bind(BorderTypeManager.class).to(RealBorderTypeManager.class);
                        bind(Protector.class).to(FallProtector.class);
                        bind(ScatterManager.class).to(RealScatterManager.class);
                    }
                }
        );

        timer = injector.getInstance(TimerCommand.class);
        translate = injector.getInstance(Translate.class);
    }

    @Test
    public void testTimerCommand() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("timer");

        Player p = mock(Player.class);
        when(p.getName()).thenReturn("testplayer");

        List<String> args = new ArrayList<String>();
        args.add("2h30m10s");
        args.add("this");
        args.add("is");
        args.add("a");
        args.add("message");

        builder.setArguments(args)
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        when(features.getFeatureByID("Timer")).thenReturn(null);
        timer.timerCommand(builder.build());
        verify(p, times(1)).sendMessage(translate.translate("timer.feature_not_found", "en"));

        TimerFeature timerFeature = mock(TimerFeature.class);
        when(features.getFeatureByID("Timer")).thenReturn(timerFeature);

        when(timerFeature.isEnabled()).thenReturn(false);
        timer.timerCommand(builder.build());
        verify(p, times(1)).sendMessage(translate.translate("timer.not_enabled", "en"));

        when(timerFeature.isEnabled()).thenReturn(true);
        when(timerFeature.startTimer(anyInt(), anyString())).thenReturn(false);
        timer.timerCommand(builder.build());
        verify(timerFeature, times(1)).startTimer(9010, "this is a message");
        verify(p, times(1)).sendMessage(translate.translate("timer.already_running", "en"));

        when(timerFeature.startTimer(anyInt(), anyString())).thenReturn(true);
        timer.timerCommand(builder.build());
        verify(timerFeature, times(2)).startTimer(9010, "this is a message");
        verify(p, times(1)).sendMessage(translate.translate("timer.running", "en"));
    }

    @Test
    public void testTimerCommandCancel() {
        CommandRequestBuilder builder = new DefaultCommandRequestBuilder(translate);

        Command command = mock(Command.class);
        when(command.getName()).thenReturn("canceltimer");

        Player p = mock(Player.class);
        when(p.getName()).thenReturn("testplayer");

        builder.setArguments(new ArrayList<String>())
                .setCount(1)
                .setLocale("en")
                .setCommand(command)
                .setSender(p);

        timer.timerCancelCommand(builder.build());
        verify(p).sendMessage(translate.translate("timer.feature_not_found", "en"));

        TimerFeature timerFeature = mock(TimerFeature.class);
        when(features.getFeatureByID("Timer")).thenReturn(timerFeature);

        timer.timerCancelCommand(builder.build());

        verify(timerFeature, times(1)).stopTimer();
        verify(p, times(1)).sendMessage(translate.translate("timer.cancelled", "en"));
    }
}
