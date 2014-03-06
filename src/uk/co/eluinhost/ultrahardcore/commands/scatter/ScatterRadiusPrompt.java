package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

public class ScatterRadiusPrompt extends NumericPrompt {

    public static final String RADIUS_DATA = "radius";

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter radius:";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Number number) {
        conversationContext.setSessionData(RADIUS_DATA, number);
        return new ScatterMinDistancePrompt();
    }

    @Override
    protected boolean isNumberValid(ConversationContext context, Number input) {
        return input.doubleValue() > 0;
    }
}
