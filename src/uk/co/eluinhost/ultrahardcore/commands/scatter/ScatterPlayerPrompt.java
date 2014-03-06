package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ScatterPlayerPrompt extends PlayerNamePrompt{

    public ScatterPlayerPrompt(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Player player) {
        return null;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return null;
    }
}
