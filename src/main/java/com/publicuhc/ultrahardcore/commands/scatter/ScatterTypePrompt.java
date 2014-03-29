package com.publicuhc.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;

public class ScatterTypePrompt extends FixedSetPrompt {

    public static final String TYPE_DATA = "scatter_type";

    /**
     * Allows any of the loaded scatter type names
     * @param manager the scatter manager
     */
    public ScatterTypePrompt(ScatterManager manager){
        super(manager.getScatterTypeNames());
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        ScatterManager scatterManager = (ScatterManager) conversationContext.getSessionData(ScatterStartPrompt.SCATTER_MANAGER);
        conversationContext.setSessionData(TYPE_DATA,scatterManager.getScatterType(s));
        return new ScatterWorldPrompt();
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter type of scatter to use: "+formatFixedSet();
    }
}
