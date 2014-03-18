package uk.co.eluinhost.testing.commands;

import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;

public class TestCommand2 {

    /**
     * Test command to run on /test.*
     * @param request the request params
     */
    @Command(trigger = "test2", identifier = "test2")
    public void testCommand(CommandRequest request){}
}
