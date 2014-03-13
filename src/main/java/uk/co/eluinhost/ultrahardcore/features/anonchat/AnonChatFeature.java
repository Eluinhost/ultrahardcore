package uk.co.eluinhost.ultrahardcore.features.anonchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class AnonChatFeature extends UHCFeature {

    public static final String ANON_BASE = BASE_PERMISSION + "anon_chat.";
    public static final String ANON_CHAT_CHAT = ANON_BASE + "chat";
    public static final String ANON_CHAT_SEE_NAME = ANON_BASE + "seeName";

    /**
     * The format before every message sent
     */
    private static final String PREFIX = ChatColor.RESET + "<" + ChatColor.MAGIC + "SECRET" + ChatColor.RESET + ">";


    /**
     * Construct an anonchat feature
     */
    public AnonChatFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin,"AnonChat","Allows players to chat without revealing their name", configManager);
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new ChatRunnable(playerName, apce.getMessage(), translate("anon_chat.no_perms")));
            }
        }
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

        /**
         * Sends the message when ran
         * @param playerName the player to run for
         * @param message the message to send
         */
        ChatRunnable(String playerName, String message, String noPermsMessage) {
            m_playerName = playerName;
            m_message = message;
            m_noPermsMessage = noPermsMessage;
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
                    pl.sendMessage(PREFIX + finalMessage);
                }
                //log the anonchat usage
                Bukkit.getLogger().info("[AnonChat][" + m_playerName + "]" + finalMessage);
            }
        }
    }
}
