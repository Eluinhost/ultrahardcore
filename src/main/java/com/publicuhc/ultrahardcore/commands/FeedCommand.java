/*
 * FeedCommand.java
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

package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
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
     * Feed themselves
     * @param request the request params
     */
    @CommandMethod
    public void feedCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        feedPlayer(player);
        player.sendMessage(translate("feed.tell", request.getLocale()));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("fed", player.getName());
        vars.put("name", player.getName());
        ServerUtil.broadcastForPermission(translate("feed.announce", request.getLocale(), vars), FEED_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /feedself.*
     * @param builder the builder
     */
    @RouteInfo
    public void feedCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feedself")
                .restrictSenderType(SenderType.PLAYER)
                .restrictPermission(FEED_SELF_PERMISSION);
    }

    /**
     * Feed another player
     * @param request request params
     */
    @CommandMethod
    public void feedOtherCommand(CommandRequest request){
        Player player = request.getPlayer(0);
        if (player == null) {
            request.getSender().sendMessage(translate("feed.invalid_player", request.getLocale(), "name", request.getFirstArg()));
            return;
        }
        feedPlayer(player);
        player.sendMessage(translate("feed.tell", request.getLocale()));
        request.sendMessage(translate("feed.fed", request.getLocale()));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("fed", player.getName());
        vars.put("name", request.getSender().getName());
        ServerUtil.broadcastForPermission(translate("feed.announce", request.getLocale(), vars), FEED_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /feed .+ except /feed *
     * @param builder the builder
     * @return the route
     */
    @RouteInfo
    public void feedOtherCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feed")
                .restrictPermission(FEED_OTHER_PERMISSION)
                .restrictArgumentCount(1, -1)
                .maxMatches(1);
    }

    /**
     * Feed all players
     * @param request request params
     */
    @CommandMethod
    public void feedAllCommand(CommandRequest request){
        for(Player player : Bukkit.getOnlinePlayers()){
            feedPlayer(player);
            player.sendMessage(translate("feed.tell", request.getLocale()));
        }
        request.getSender().sendMessage(translate("feed.fed_all", request.getLocale()));
        ServerUtil.broadcastForPermission(translate("feed.fed_all_announce", request.getLocale(), "name", request.getSender().getName()), FEED_ANNOUNCE_PERMISSION);
    }

    /**
     * Match only on /feed *
     * @param builder the builder
     */
    @RouteInfo
    public void feedAllCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("feed")
                .restrictPermission(FEED_OTHER_PERMISSION)
                .restrictStartsWith("*");
    }
}
