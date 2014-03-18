package com.publicuhc.ultrahardcore.commands.scatter;

import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class ScatterUseTeamsPrompt extends BooleanPrompt{

    public static final String TEAMS_DATA = "teams";

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, boolean bool) {
        conversationContext.setSessionData(TEAMS_DATA, bool);
        return new ScatterPlayerPrompt();
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Scatter as teams?";
    }
}
