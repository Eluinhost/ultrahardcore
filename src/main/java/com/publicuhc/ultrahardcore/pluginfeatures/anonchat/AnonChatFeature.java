/*
 * AnonChatFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.anonchat;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@Singleton
public class AnonChatFeature extends UHCFeature {

    public static final String ANON_BASE = BASE_PERMISSION + "AnonChat.";
    public static final String ANON_CHAT_CHAT = ANON_BASE + "chat";
    public static final String ANON_CHAT_SEE_NAME = ANON_BASE + "seeName";

    /**
     * The format before every message sent
     */
    private static final String PREFIX = ChatColor.RESET + "<" + ChatColor.MAGIC + "SECRET" + ChatColor.RESET + ">";


    /**
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private AnonChatFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * On player chat events
     * @param apce AsyncPlayerChatEvent
     */
    @EventHandler
    public void onAsyncChatEvent(AsyncPlayerChatEvent apce) {
        //only if we are enabled
        if (isEnabled()) {
            //if the message started with a P
            if(apce.getMessage().startsWith("P ")){
                apce.setCancelled(true);
                //schedule the new message to be send on the next tick
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new ChatRunnable(apce.getPlayer().getUniqueId(), apce.getMessage().substring(2), "anon_chat.no_perms", getTranslate()));
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "AnonChat";
    }

    @Override
    public String getDescription() {
        return "Allows players to chat without revealing their name";
    }

    private static class ChatRunnable implements Runnable {

        /**
         * The player sending the message
         */
        private final UUID m_playerID;
        /**
         * The message they are sending
         */
        private final String m_message;

        private final String m_noPermsMessage;

        private final Translate m_translate;

        /**
         * Sends the message when ran
         * @param playerID the player to run for
         * @param message the message to send
         * @param noPermsMessage the message to say for no permissions
         * @param translate the translator
         */
        ChatRunnable(UUID playerID, String message, String noPermsMessage, Translate translate) {
            m_playerID = playerID;
            m_message = message;
            m_noPermsMessage = noPermsMessage;
            m_translate = translate;
        }

        @Override
        public void run() {
            //attempt to get the player from the stored name if it exists
            Player p = Bukkit.getPlayer(m_playerID);
            if (null == p) {
                return;
            }
            //check the permissions
            if (!p.hasPermission(ANON_CHAT_CHAT)) {
                p.sendMessage(m_noPermsMessage);
                return;
            }

            //create the message and send it to all players online
            String finalMessage = PREFIX+m_message;
            for (Player pl : Bukkit.getOnlinePlayers()) {
                //check for the see name permission and send the actual name along with the message
                if (pl.hasPermission(ANON_CHAT_SEE_NAME)) {
                    pl.sendMessage(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + p.getName() + finalMessage);
                } else {
                    pl.sendMessage(PREFIX + m_translate.translate(finalMessage, m_translate.getLocaleForSender(pl)));
                }
                //log the anonchat usage
                Bukkit.getLogger().info("[AnonChat][" + p.getName() + "]" + finalMessage);
            }
        }
    }
}
