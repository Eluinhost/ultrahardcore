package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;

public class ScatterAbandonListener implements ConversationAbandonedListener {
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        if(!abandonedEvent.gracefulExit()){
            abandonedEvent.getContext().getForWhom().sendRawMessage(ChatColor.RED+"Command cancelled");
        }
    }
}
