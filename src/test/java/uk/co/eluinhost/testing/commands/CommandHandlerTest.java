package uk.co.eluinhost.testing.commands;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.co.eluinhost.DummyJavaPlugin;
import uk.co.eluinhost.commands.CommandHandler;
import uk.co.eluinhost.commands.exceptions.CommandCreateException;
import uk.co.eluinhost.commands.exceptions.CommandIDConflictException;
import uk.co.eluinhost.commands.exceptions.CommandParentNotFoundException;
import uk.co.eluinhost.commands.exceptions.InvalidMethodParametersException;
import uk.co.eluinhost.features.FeatureManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FeatureManager.class)
public class CommandHandlerTest {

    @Test( expected = CommandIDConflictException.class)
    public void testCommandConflict() throws CommandIDConflictException, InvalidMethodParametersException, CommandParentNotFoundException, CommandCreateException {
        CommandHandler handler = CommandHandler.getInstance();

        handler.registerCommands(TestCommand.class, new DummyJavaPlugin());
        handler.registerCommands(TestCommand.class, new DummyJavaPlugin());
    }
}
