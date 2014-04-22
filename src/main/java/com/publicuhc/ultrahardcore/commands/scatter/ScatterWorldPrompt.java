package com.publicuhc.ultrahardcore.commands.scatter;

import com.publicuhc.pluginframework.commands.prompts.WorldNamePrompt;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class ScatterWorldPrompt extends WorldNamePrompt {

    public static final String WORLD_DATA = "world";

    /**
     * Allow any loaded world
     */
    public ScatterWorldPrompt(){
        super(ServerUtil.getWorldNames());
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter the name of the world to scatter into: "+formatFixedSet();
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, World world) {
        conversationContext.setSessionData(WORLD_DATA,world);
        return new ScatterCenterPrompt();
    }
}
