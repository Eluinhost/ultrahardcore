package uk.co.eluinhost.ultrahardcore.commands.teststructure;

public class TestCommand {

    @Command(name="test", allowedTypes = {SenderType.CONSOLE, SenderType.REMOTE_CONSOLE})
    public void testCommandMethod(CommandRequest request){
        //TODO
    }
}
