package uk.co.eluinhost.ultrahardcore.commands.inter;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;

//TODO wtf is this even
public class CommandCancelHandler implements ConversationAbandonedListener {

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        if (!abandonedEvent.gracefulExit()) {
            abandonedEvent.getContext().getForWhom().sendRawMessage(ChatColor.RED + "Command cancelled");
        }
    }
}

