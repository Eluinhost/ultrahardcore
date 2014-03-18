package uk.co.eluinhost.testing.commands;

import com.google.inject.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.co.eluinhost.commands.*;
import uk.co.eluinhost.commands.exceptions.*;
import uk.co.eluinhost.features.FeatureManager;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class,PluginCommand.class})
public class CommandHandlerTest {

    @Test( expected = CommandIDConflictException.class )
    public void testCommandIDConficts() throws CommandIDConflictException, InvalidMethodParametersException, CommandParentNotFoundException, CommandCreateException {

        mockStatic(Bukkit.class);
        when(Bukkit.getPluginCommand("test")).thenReturn(mock(PluginCommand.class));

        Injector injector = Guice.createInjector(new ModuleDeps());

        CommandHandler handler = injector.getInstance(CommandHandler.class);

        handler.registerCommands(TestCommand.class);
        handler.registerCommands(TestCommand.class);
    }

    @Test
    public void testNonConflictingIDs() throws CommandIDConflictException, InvalidMethodParametersException, CommandParentNotFoundException, CommandCreateException {
        mockStatic(Bukkit.class);
        when(Bukkit.getPluginCommand("test")).thenReturn(mock(PluginCommand.class));
        when(Bukkit.getPluginCommand("test2")).thenReturn(mock(PluginCommand.class));

        Injector injector = Guice.createInjector(new ModuleDeps());

        CommandHandler handler = injector.getInstance(CommandHandler.class);

        handler.registerCommands(TestCommand.class);
        handler.registerCommands(TestCommand2.class);
    }

    private class ModuleDeps extends AbstractModule {

        @Override
        protected void configure() {
            bind(CommandHandler.class).to(RealCommandHandler.class);
            bind(CommandMap.class).to(RealCommandMap.class);
        }
    }
}
