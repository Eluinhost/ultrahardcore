package uk.co.eluinhost.ultrahardcore.commands.teststructure;

public class TestCommand {

    /**
     * runs with '/test'
     * @param request
     */
    @Command(trigger="test", identifier="TestCommand-root")
    public void testCommandMethod(CommandRequest request){}

    /**
     * runs with '/test sub-1'
     * @param request
     */
    @Command(trigger="sub-1", identifier="TestCommand-sub-1", parentID = "TestCommand-root")
    public void testCommandMethod2(CommandRequest request){}

    /**
     * runs with '/test sub-2'
     * @param request
     */
    @Command(trigger="sub-2", identifier="TestCommand-sub-2", parentID = "TestCommand-root")
    public void testCommandMethod3(CommandRequest request){}
}
