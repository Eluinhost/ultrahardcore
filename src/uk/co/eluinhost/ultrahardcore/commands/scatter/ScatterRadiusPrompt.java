package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

public class ScatterRadiusPrompt extends NumericPrompt {
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter radius:";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Number number) {
        return null;
    }
}
