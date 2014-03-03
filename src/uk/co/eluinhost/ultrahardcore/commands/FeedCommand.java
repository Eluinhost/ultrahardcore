package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.commands.SenderType;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class FeedCommand {

    public static final float MAX_SATURATION = 5.0F;
    public static final int MAX_FOOD_LEVEL = 20;

    public static final String FEED_SELF_PERMISSION = "UHC.feed.self";
    public static final String FEED_ANNOUNCE_PERMISSION = "UHC.feed.announce";
    public static final String FEED_OTHER_PERMISSION = "UHC.feed.other";

    /**
     * Feeds a player to full hunger and saturation and resets exhaustion
     * @param player player to feed
     */
    public static void feedPlayer(Player player){
        player.setFoodLevel(MAX_FOOD_LEVEL);
        player.setExhaustion(0.0F);
        player.setSaturation(MAX_SATURATION);
    }

    /**
     * Ran on /feedself
     * @param request request params
     */
    @Command(trigger = "feedself",
            identifier = "FeedSelfCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = FEED_SELF_PERMISSION,
            senders = {SenderType.PLAYER})
    public void onFeedCommand(CommandRequest request){
        Player player = (Player) request.getSender();
            feedPlayer(player);
            player.sendMessage(ChatColor.GOLD + "You fed yourself to full hunger");
            ServerUtil.broadcastForPermission(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] Player " + player.getName() + " used a feed command to feed themselves", FEED_ANNOUNCE_PERMISSION);
    }

    /**
     * Ran on /feed {name}
     * @param request request params
     */
    @Command(trigger = "feed",
            identifier = "FeedCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = FEED_OTHER_PERMISSION)
    public void onFeedOtherCommand(CommandRequest request){
        Player player = Bukkit.getPlayer(request.getFirstArg());
        if (player == null) {
            request.getSender().sendMessage(ChatColor.RED + "Invalid player name " + request.getFirstArg());
            return;
        }
        feedPlayer(player);
        player.sendMessage(ChatColor.GOLD + "You were fed to full hunger");
        ServerUtil.broadcastForPermission(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] " +
                (request.getSender() instanceof Player ? "Player " + request.getSender().getName() : "Console") + " fed player " + player.getName()
                , FEED_ANNOUNCE_PERMISSION);
    }

    /**
     * Ran on /feed *
     * @param request request params
     */
    @Command(trigger = "*",
            identifier = "FeedAllCommand",
            parentID = "FeedCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = FEED_OTHER_PERMISSION)
    public void onFeedAllCommand(CommandRequest request){
        for(Player player : Bukkit.getOnlinePlayers()){
            feedPlayer(player);
            player.sendMessage(ChatColor.GOLD + "You were fed to full hunger");
        }
        request.getSender().sendMessage(ChatColor.GOLD+"Fed all players.");
        ServerUtil.broadcastForPermission(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] " +
                (request.getSender() instanceof Player ? "Player " + request.getSender().getName() : "Console") + " fed all players"
                , FEED_ANNOUNCE_PERMISSION);
    }
}
