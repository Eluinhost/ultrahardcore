package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routing.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
     * Feed themselves
     * @param request the request params
     */
    @CommandMethod
    public void feedCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        feedPlayer(player);
        player.sendMessage(translate("feed.tell", locale(request.getSender())));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("fed", player.getName());
        vars.put("name", player.getName());
        ServerUtil.broadcastForPermission(translate("feed.announce", locale(request.getSender()), vars), FEED_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /feedself.*
     * @param builder the builder
     */
    @RouteInfo
    public void feedCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feedself");
        builder.restrictSenderType(SenderType.PLAYER);
        builder.restrictPermission(FEED_SELF_PERMISSION);
    }

    /**
     * Feed another player
     * @param request request params
     */
    @CommandMethod
    public void feedOtherCommand(CommandRequest request){
        Player player = Bukkit.getPlayer(request.getFirstArg());
        if (player == null) {
            request.getSender().sendMessage(translate("feed.invalid_player", locale(request.getSender()), "name", request.getFirstArg()));
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
     * Run on /feed .+ except /feed *
     * @param builder the builder
     * @return the route
     */
    @RouteInfo
    public void feedOtherCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feed");
        builder.restrictPermission(FEED_OTHER_PERMISSION);
        builder.restrictPattern(Pattern.compile("[^*]+"));
    }

    /**
     * Feed all players
     * @param request request params
     */
    @CommandMethod
    public void feedAllCommand(CommandRequest request){
        for(Player player : Bukkit.getOnlinePlayers()){
            feedPlayer(player);
            player.sendMessage(translate("feed.tell", locale(request.getSender())));
        }
        request.getSender().sendMessage(translate("feed.fed_all", locale(request.getSender())));
        ServerUtil.broadcastForPermission(translate("feed.fed_all_announce", locale(request.getSender()), "name", request.getSender().getName()), FEED_ANNOUNCE_PERMISSION);
    }

    /**
     * Match only on /feed *
     * @param builder the builder
     */
    @RouteInfo
    public void feedAllCommandDetails(RouteBuilder builder) {
        builder.restrictPattern(Pattern.compile("\\*"));
        builder.restrictCommand("feed");
        builder.restrictPermission(FEED_OTHER_PERMISSION);
    }
}
