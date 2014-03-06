package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import uk.co.eluinhost.commands.conversations.WorldNamePrompt;

public class ScatterWorldPrompt extends WorldNamePrompt {

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return null;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, World world) {
        return null;
    }
}
