package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

public class ScatterMinDistancePrompt extends NumericPrompt {

    public static final String MIN_DIST_DATA = "min_dist";

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Number number) {
        conversationContext.setSessionData(MIN_DIST_DATA, number.doubleValue());
        return new ScatterUseTeamsPrompt();
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter minimum distance between scatters";
    }

    @Override
    protected boolean isNumberValid(ConversationContext context, Number input) {
        return input.doubleValue() >= 0;
    }
}
