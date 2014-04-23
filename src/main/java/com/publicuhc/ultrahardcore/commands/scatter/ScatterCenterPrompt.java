package com.publicuhc.ultrahardcore.commands.scatter;

import com.publicuhc.pluginframework.commands.prompts.XZCoordinatePrompt;
import com.publicuhc.pluginframework.util.SimplePair;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class ScatterCenterPrompt extends XZCoordinatePrompt {

    public static final String CENTER_DATA = "center";

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter center coords: x,z";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, SimplePair<Double, Double> coords) {
        conversationContext.setSessionData(CENTER_DATA,coords);
        return new ScatterRadiusPrompt();
    }
}
