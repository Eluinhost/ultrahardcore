package uk.co.eluinhost.testing.commands;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;

public class TestCommand {

    /**
     * Test command to run on /test.*
     * @param request the request params
     */
    @Command(trigger = "test", identifier = "test")
    public void testCommand(CommandRequest request){}
}
