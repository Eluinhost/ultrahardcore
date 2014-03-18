package com.publicuhc.ultrahardcore.commands.scatter;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

public class ScatterPrefix implements ConversationPrefix {
    @Override
    public String getPrefix(ConversationContext conversationContext) {
        return String.valueOf(ChatColor.GOLD);
    }
}
