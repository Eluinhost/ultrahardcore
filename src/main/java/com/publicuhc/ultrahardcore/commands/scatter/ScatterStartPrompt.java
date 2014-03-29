package com.publicuhc.ultrahardcore.commands.scatter;

import com.publicuhc.commands.CommandHandler;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

/**
 * Project: UHC
 * Package: com.publicuhc.ultrahardcore.commands.scatter
 * Created by Eluinhost on 15:57 29/03/14 2014.
 */
public class ScatterStartPrompt extends MessagePrompt {

    public static final String CONFIG_MANAGER = "command_handler";
    public static final String SCATTER_MANAGER = "scatter_manager";

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return new ScatterTypePrompt((ScatterManager) conversationContext.getSessionData(SCATTER_MANAGER));
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Starting interactive scatter. You can end this command at any point by tying 'cancel' or by waiting.";
    }
}
