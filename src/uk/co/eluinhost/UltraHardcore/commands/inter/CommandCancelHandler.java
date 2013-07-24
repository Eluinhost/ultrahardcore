package uk.co.eluinhost.UltraHardcore.commands.inter;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;

public class CommandCancelHandler implements ConversationAbandonedListener {

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent conversationAbandonedEvent) {
        if(!conversationAbandonedEvent.gracefulExit()){
            conversationAbandonedEvent.getContext().getForWhom().sendRawMessage(ChatColor.RED+"Command cancelled");
        }
    }
}

