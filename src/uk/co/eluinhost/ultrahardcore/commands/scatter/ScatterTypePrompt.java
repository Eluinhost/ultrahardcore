package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

public class ScatterTypePrompt extends FixedSetPrompt {

    public ScatterTypePrompt(){
        super(/*TODO types*/);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        return null;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return null;
    }
}
