package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import uk.co.eluinhost.commands.conversations.XZCoordinatePrompt;
import uk.co.eluinhost.ultrahardcore.util.SimplePair;

public class ScatterCenterPrompt extends XZCoordinatePrompt {

    private static final String PROMPT_TEXT = "Enter center coords: x,z";

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return null;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, SimplePair<Double, Double> coords) {
        return null;
    }
}
