package com.publicuhc.ultrahardcore.features.anonchat;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

@Singleton
public class AnonChatFeature extends UHCFeature {

    public static final String ANON_BASE = BASE_PERMISSION + "anon_chat.";
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
                //get the player name and cancel the event
                String playerName = apce.getPlayer().getName();
                apce.setCancelled(true);
                //schedule the new message to be send on the next tick
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new ChatRunnable(playerName, apce.getMessage(), "anon_chat.no_perms", getTranslate()));
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
         * The player name sending the message
         */
        private final String m_playerName;
        /**
         * The message they are sending
         */
        private final String m_message;

        private final String m_noPermsMessage;

        private final Translate m_translate;

        /**
         * Sends the message when ran
         * @param playerName the player to run for
         * @param message the message to send
         * @param noPermsMessage the message to say for no permissions
         * @param translate the translator
         */
        ChatRunnable(String playerName, String message, String noPermsMessage, Translate translate) {
            m_playerName = playerName;
            m_message = message;
            m_noPermsMessage = noPermsMessage;
            m_translate = translate;
        }

        @Override
        public void run() {
            //attempt to get the player from the stored name if it exists
            Player p = Bukkit.getPlayerExact(m_playerName);
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
                    pl.sendMessage(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + m_playerName + finalMessage);
                } else {
                    pl.sendMessage(PREFIX + m_translate.translate(finalMessage, m_translate.getLocaleForSender(pl)));
                }
                //log the anonchat usage
                Bukkit.getLogger().info("[AnonChat][" + m_playerName + "]" + finalMessage);
            }
        }
    }
}
