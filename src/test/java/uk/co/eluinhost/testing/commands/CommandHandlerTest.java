package uk.co.eluinhost.testing.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.internal.PowerMockitoCore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.co.eluinhost.commands.CommandHandler;
import uk.co.eluinhost.commands.exceptions.CommandCreateException;
import uk.co.eluinhost.commands.exceptions.CommandIDConflictException;
import uk.co.eluinhost.commands.exceptions.CommandParentNotFoundException;
import uk.co.eluinhost.commands.exceptions.InvalidMethodParametersException;
import uk.co.eluinhost.features.FeatureManager;

import java.util.logging.Logger;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FeatureManager.class,Bukkit.class,PluginCommand.class})
public class CommandHandlerTest {

    @Test( expected = CommandIDConflictException.class)
    public void testCommandConflict() throws CommandIDConflictException, InvalidMethodParametersException, CommandParentNotFoundException, CommandCreateException {
        CommandHandler handler = new CommandHandler(Logger.getAnonymousLogger());

        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("test");
        mockStatic(Bukkit.class);
        when(Bukkit.getPluginCommand("test")).thenReturn(command);
        when(Bukkit.getLogger()).thenReturn(Logger.getAnonymousLogger());

        handler.registerCommands(TestCommand.class);
        handler.registerCommands(TestCommand.class);
    }
}
