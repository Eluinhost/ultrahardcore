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

package com.publicuhc.ultrahardcore.core.commands;

import com.publicuhc.pluginframework.routing.annotation.*;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class FeedCommand implements Command
{

    public static final float MAX_SATURATION = 5.0F;
    public static final int MAX_FOOD_LEVEL = 20;

    public static final String FEED_SELF_PERMISSION = "UHC.feed.self";
    public static final String FEED_ANNOUNCE_PERMISSION = "UHC.feed.announce";
    public static final String FEED_OTHER_PERMISSION = "UHC.feed.other";

    private final Translate translate;

    @Inject
    private FeedCommand(Translate translate)
    {
        this.translate = translate;
    }

    /**
     * Feeds a player to full hunger and saturation and resets exhaustion
     *
     * @param player player to feed
     */
    public void feedPlayer(Player player)
    {
        player.setFoodLevel(MAX_FOOD_LEVEL);
        player.setExhaustion(0.0F);
        player.setSaturation(MAX_SATURATION);
        translate.sendMessage("feed.tell", player);
    }

    @CommandMethod("feedself")
    @PermissionRestriction(FEED_SELF_PERMISSION)
    @SenderRestriction(Player.class)
    public void feedCommand(OptionSet set, Player sender)
    {
        feedPlayer(sender);
        translate.broadcastMessageForPermission(FEED_ANNOUNCE_PERMISSION, "feed.announce_self", sender.getName(), 1);
    }

    @CommandMethod("feed")
    @PermissionRestriction(FEED_OTHER_PERMISSION)
    @CommandOptions("[arguments]")
    public void feedOtherCommand(OptionSet set, CommandSender sender, List<Player[]> args)
    {
        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);

        if(players.isEmpty()) {
            translate.sendMessage("supply one player name", sender);
            return;
        }

        for(Player player : players) {
            feedPlayer(player);
        }

        translate.sendMessage("feed.fed", sender, players.size());
        translate.broadcastMessageForPermission(FEED_ANNOUNCE_PERMISSION, "feed.announce", sender.getName(), players.size());
    }

    @OptionsMethod
    public void feedOtherCommand(OptionDeclarer parser)
    {
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
