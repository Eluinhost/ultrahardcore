package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import uk.co.eluinhost.commands.conversations.XZCoordinatePrompt;
import uk.co.eluinhost.ultrahardcore.util.SimplePair;

public class ScatterCenterPrompt extends XZCoordinatePrompt {

    public static final String CENTER_DATA = "center";

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter center coords: x,z";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, SimplePair<Double, Double> coords) {
        conversationContext.setSessionData(CENTER_DATA,coords);
        return new ScatterRadiusPrompt();
    }
}
