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

import com.publicuhc.pluginframework.routing.annotation.*;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

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

    @CommandMethod("feedself")
    @PermissionRestriction(FEED_SELF_PERMISSION)
    @SenderRestriction(Player.class)
    public void feedCommand(OptionSet set, Player sender){
        feedPlayer(sender);
        sender.sendMessage(translate("feed.tell", sender));
        ServerUtil.broadcastForPermission(translate("feed.announce", sender, sender.getName(), sender.getName()), FEED_ANNOUNCE_PERMISSION);
    }

    @CommandMethod("feed")
    @PermissionRestriction(FEED_OTHER_PERMISSION)
    @CommandOptions("[arguments]")
    public void feedOtherCommand(OptionSet set, CommandSender sender, List<Player[]> args) {
        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);

        for(Player player : players) {
            feedPlayer(player);
            player.sendMessage(translate("feed.tell", player));
        }

        ServerUtil.broadcastForPermission(translate("feed.announce", sender, players.toString(), sender.getName()), FEED_ANNOUNCE_PERMISSION);
    }

    @OptionsMethod
    public void feedOtherCommand(OptionDeclarer parser)
    {
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
