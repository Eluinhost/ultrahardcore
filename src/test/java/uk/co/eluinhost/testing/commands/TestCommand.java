package uk.co.eluinhost.testing.commands;

import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;

public class TestCommand {

    /**
     * Test command to run on /test.*
     * @param request the request params
     */
    @Command(trigger = "test", identifier = "test")
    public void testCommand(CommandRequest request){}
}
