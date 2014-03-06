package uk.co.eluinhost.commands.conversations;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

public abstract class WorldNamePrompt extends ValidatingPrompt {

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        World world = Bukkit.getWorld(s);
        return world != null;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        return acceptValidatedInput(conversationContext, Bukkit.getWorld(s));
    }

    /**
     * @param conversationContext the context
     * @param world the accepted world
     * @return the next prompt
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext conversationContext, World world);
}
