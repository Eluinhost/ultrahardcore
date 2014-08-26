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

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.OptionsMethod;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class FeedCommand extends TranslatingCommand {

    public static final float MAX_SATURATION = 5.0F;
    public static final int MAX_FOOD_LEVEL = 20;

    public static final String FEED_SELF_PERMISSION = "UHC.feed.self";
    public static final String FEED_ANNOUNCE_PERMISSION = "UHC.feed.announce";
    public static final String FEED_OTHER_PERMISSION = "UHC.feed.other";

    @Inject
    private FeedCommand(Translate translate) {
        super(translate);
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
    @CommandMethod(command = "feedself", permission = FEED_SELF_PERMISSION, allowedSenders = Player.class)
    public void feedCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        feedPlayer(player);
        player.sendMessage(translate("feed.tell", request.getSender()));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("fed", player.getName());
        vars.put("name", player.getName());
        ServerUtil.broadcastForPermission(translate("feed.announce", request.getSender(), vars), FEED_ANNOUNCE_PERMISSION);
    }

    @CommandMethod(command = "feed", permission = FEED_OTHER_PERMISSION, options = true)
    public void feedOtherCommand(CommandRequest request){
        Iterable<Player[]> playersList = (Iterable<Player[]>) request.getOptions().nonOptionArguments();
        Collection<Player> players = new HashSet<Player>();
        for(Player[] comboPlayers : playersList) {
            Collections.addAll(players, comboPlayers);
        }

        for(Player player : players) {
            feedPlayer(player);
            player.sendMessage(translate("feed.tell", player));
        }

        Map<String, String> vars = new HashMap<String, String>();
        vars.put("fed", players.toString());
        vars.put("name", request.getSender().getName());
        ServerUtil.broadcastForPermission(translate("feed.announce", request.getSender(), vars), FEED_ANNOUNCE_PERMISSION);
    }

    @OptionsMethod
    public void feedOtherCommand(OptionDeclarer parser)
    {
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
