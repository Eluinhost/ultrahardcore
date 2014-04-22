package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.commands.SenderType;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FeedCommand extends SimpleCommand {

    public static final float MAX_SATURATION = 5.0F;
    public static final int MAX_FOOD_LEVEL = 20;

    public static final String FEED_SELF_PERMISSION = "UHC.feed.self";
    public static final String FEED_ANNOUNCE_PERMISSION = "UHC.feed.announce";
    public static final String FEED_OTHER_PERMISSION = "UHC.feed.other";

    @Inject
    private FeedCommand(Configurator configManager, Translate translate) {
        super(configManager, translate);
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
        player.sendMessage(translate("feed.tell", locale(request.getSender())));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("fed", player.getName());
        vars.put("name", player.getName());
        ServerUtil.broadcastForPermission(translate("feed.announce", locale(request.getSender()), vars), FEED_ANNOUNCE_PERMISSION);
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
            request.getSender().sendMessage(translate("feed.invalid_player", locale(request.getSender()), "name",request.getFirstArg()));
            return;
        }
        feedPlayer(player);
        player.sendMessage(translate("feed.tell", locale(request.getSender())));
        request.sendMessage(translate("feed.fed", locale(request.getSender())));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("fed", player.getName());
        vars.put("name", request.getSender().getName());
        ServerUtil.broadcastForPermission(translate("feed.announce", locale(request.getSender()), vars), FEED_ANNOUNCE_PERMISSION);
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
            player.sendMessage(translate("feed.tell", locale(request.getSender())));
        }
        request.getSender().sendMessage(translate("feed.fed_all", locale(request.getSender())));
        ServerUtil.broadcastForPermission(translate("feed.fed_all_announce", locale(request.getSender()), "name", request.getSender().getName()), FEED_ANNOUNCE_PERMISSION);
    }
}
