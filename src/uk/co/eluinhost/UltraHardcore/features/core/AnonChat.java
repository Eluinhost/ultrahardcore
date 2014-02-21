package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class AnonChat extends UHCFeature {

    private static final String PREFIX = ChatColor.RESET + "<" + ChatColor.MAGIC + "SECRET" + ChatColor.RESET + ">";

    public AnonChat(boolean enabled) {
        super("AnonChat", enabled);
        setDescription("Allows players to chat without revealing their name");
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncPlayerChatEvent apce) {
        if (isEnabled()) {
            String[] tokens = apce.getMessage().split(" ");
            if (tokens.length > 0 && "P".equals(tokens[0])) {
                String playerName = apce.getPlayer().getName();
                apce.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(), new ChatRunnable(playerName, tokens));
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

        public static final int CAPACITY = 256;
        private final String m_playerName;
        private final String[] m_tokens;

        ChatRunnable(String playerName, String[] tokens) {
            m_playerName = playerName;
            m_tokens = tokens;
        }

        @Override
        public void run() {
            Player p = Bukkit.getPlayer(m_playerName);
            if (p == null) {
                return;
            }
            if (!p.hasPermission(PermissionNodes.ANON_CHAT_CHAT)) {
                p.sendMessage(ChatColor.RED + "You don't have the permissions to use annonymous chat");
                return;
            }
            StringBuilder buf = new StringBuilder(CAPACITY);
            buf.append(PREFIX);
            for (int i = 1; i < m_tokens.length; i++) {
                buf.append(" ");
                buf.append(m_tokens[i]);
            }
            String finalMessage = buf.toString();
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (pl.hasPermission(PermissionNodes.ANON_CHAT_SEE_NAME)) {
                    pl.sendMessage(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + m_playerName + finalMessage);
                } else {
                    pl.sendMessage(PREFIX + finalMessage);
                }
                Bukkit.getLogger().info("[AnonChat][" + m_playerName + "]" + finalMessage);
            }
        }
    }
}
