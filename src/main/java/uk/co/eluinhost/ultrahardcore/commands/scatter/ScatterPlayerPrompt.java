package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class ScatterPlayerPrompt extends StringPrompt {

    public static final String PLAYERS_DATA = "players";

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter a player name to scatter, '*' to scatter all or 'END' to start the scatter";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        Object data = conversationContext.getSessionData(PLAYERS_DATA);
        Set<Player> players;
        if(data == null){
            players = Collections.newSetFromMap(new WeakHashMap<Player,Boolean>());
            conversationContext.setSessionData(PLAYERS_DATA,players);
        }else{
            players = (Set<Player>) data;
        }
        if("END".equalsIgnoreCase(s)){
            if(players.isEmpty()){
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"You must specify at least 1 player!");
            }else{
                return new ScatterEndPrompt();
            }
        }

        if("*".equalsIgnoreCase(s)){
            Collections.addAll(players, Bukkit.getOnlinePlayers());
            return new ScatterPlayerPrompt();
        }


        Player p = Bukkit.getPlayer(s);
        if (p != null) {
            players.add(p);
        } else {
            conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"Couldn't find the player "+s);
        }
        return new ScatterPlayerPrompt();
    }
}
