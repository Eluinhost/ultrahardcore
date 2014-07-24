/*
 * ScatterPlayerPrompt.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.commands.scatter;

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
            players.addAll(Bukkit.getOnlinePlayers());
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
