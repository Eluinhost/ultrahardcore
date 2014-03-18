package com.publicuhc.ultrahardcore.commands;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.commands.SenderType;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.util.ServerUtil;

public class FeedCommand extends SimpleCommand {

    public static final float MAX_SATURATION = 5.0F;
    public static final int MAX_FOOD_LEVEL = 20;

    public static final String FEED_SELF_PERMISSION = "UHC.feed.self";
    public static final String FEED_ANNOUNCE_PERMISSION = "UHC.feed.announce";
    public static final String FEED_OTHER_PERMISSION = "UHC.feed.other";

    @Inject
    private FeedCommand(ConfigManager configManager) {
        super(configManager);
    }

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
        player.sendMessage(translate("feed.tell"));
        ServerUtil.broadcastForPermission(translate("feed.announce").replaceAll("%name%",player.getName()).replaceAll("%fed%",player.getName()), FEED_ANNOUNCE_PERMISSION);
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
            request.getSender().sendMessage(translate("feed.invalid_player").replaceAll("%name%",request.getFirstArg()));
            return;
        }
        feedPlayer(player);
        player.sendMessage(translate("feed.tell"));
        request.sendMessage(translate("feed.fed"));
        ServerUtil.broadcastForPermission(translate("feed.announce").replaceAll("%name%",request.getSender().getName()).replaceAll("%fed%",player.getName()), FEED_ANNOUNCE_PERMISSION);
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
            player.sendMessage(translate("feed.tell"));
        }
        request.getSender().sendMessage(translate("feed.fed_all"));
        ServerUtil.broadcastForPermission(translate("feed.fed_all_announce").replaceAll("%name%",request.getSender().getName()), FEED_ANNOUNCE_PERMISSION);
    }
}
