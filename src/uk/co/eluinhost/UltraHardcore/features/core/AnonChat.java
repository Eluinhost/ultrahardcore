package uk.co.eluinhost.ultrahardcore.features.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class AnonChat extends UHCFeature {

    /**
     * The format before every message sent
     */
    private static final String PREFIX = ChatColor.RESET + "<" + ChatColor.MAGIC + "SECRET" + ChatColor.RESET + ">";

    /**
     * Construct an anonchat feature
     * @param enabled enabled by default or not
     */
    public AnonChat(boolean enabled) {
        super("AnonChat", enabled);
        setDescription("Allows players to chat without revealing their name");
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(), new ChatRunnable(playerName, apce.getMessage()));
            }
        }
    }


    @Override
    public void enableFeature() {
    }

    @Override
    public void disableFeature() {
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

        ChatRunnable(String playerName, String message) {
            m_playerName = playerName;
            m_message = message;
        }

        @Override
        public void run() {
            //attempt to get the player from the stored name if it exists
            Player p = Bukkit.getPlayerExact(m_playerName);
            if (null == p) {
                return;
            }
            //check the permissions
            if (!p.hasPermission(PermissionNodes.ANON_CHAT_CHAT)) {
                p.sendMessage(ChatColor.RED + "You don't have the permissions to use annonymous chat");
                return;
            }

            //create the message and send it to all players online
            String finalMessage = PREFIX+m_message;
            for (Player pl : Bukkit.getOnlinePlayers()) {
                //check for the see name permission and send the actual name along with the message
                if (pl.hasPermission(PermissionNodes.ANON_CHAT_SEE_NAME)) {
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
