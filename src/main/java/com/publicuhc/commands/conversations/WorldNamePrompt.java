package com.publicuhc.commands.conversations;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

public abstract class WorldNamePrompt extends FixedSetPrompt {

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        World world = Bukkit.getWorld(input);
        return world != null && isWorldValid(world);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        return acceptValidatedInput(conversationContext, Bukkit.getWorld(s));
    }

    /**
     * @param worlds the worlds to allow
     */
    protected WorldNamePrompt(String... worlds){
        super(worlds);
    }

    /**
     * @param conversationContext the context
     * @param world the accepted world
     * @return the next prompt
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext conversationContext, World world);

    /**
     * @param world the world to check
     * @return true if world valid, false if not
     */
    protected boolean isWorldValid(World world){
        return true;
    }
}
