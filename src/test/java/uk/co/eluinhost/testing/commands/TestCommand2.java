package uk.co.eluinhost.testing.commands;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;

public class TestCommand2 {

    /**
     * Test command to run on /test.*
     * @param request the request params
     */
    @Command(trigger = "test2", identifier = "test2")
    public void testCommand(CommandRequest request){}
}
