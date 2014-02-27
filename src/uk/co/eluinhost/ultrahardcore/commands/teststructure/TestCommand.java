package uk.co.eluinhost.ultrahardcore.commands.teststructure;

public class TestCommand {

    @Command(name="test", id="test-root", allowedTypes = {SenderType.CONSOLE, SenderType.REMOTE_CONSOLE})
    public void testCommandMethod(CommandRequest request){
        //TODO
    }

    @Command(name="subtest1", id="test-sub-1", parentID = "test-root", allowedTypes = {SenderType.CONSOLE, SenderType.REMOTE_CONSOLE})
    public void testCommandMethod2(CommandRequest request){
        //TODO
    }

    @Command(name="subtest2", id="test-sub-2", parentID = "test-root", allowedTypes = {SenderType.CONSOLE, SenderType.REMOTE_CONSOLE})
    public void testCommandMethod3(CommandRequest request){
        //TODO
    }
}
