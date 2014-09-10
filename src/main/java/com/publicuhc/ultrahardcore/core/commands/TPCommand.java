/*
 * TPCommand.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.ultrahardcore.core.commands;

import com.publicuhc.pluginframework.routing.annotation.CommandMethod;
import com.publicuhc.pluginframework.routing.annotation.CommandOptions;
import com.publicuhc.pluginframework.routing.annotation.OptionsMethod;
import com.publicuhc.pluginframework.routing.annotation.PermissionRestriction;
import com.publicuhc.pluginframework.routing.converters.LocationValueConverter;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Command;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class TPCommand implements Command
{

    public static final String TP_ALL_PERMISSION = "UHC.tpall";

    private final Translate translate;

    @Inject
    private TPCommand(Translate translate)
    {
        this.translate = translate;
    }

    @CommandMethod("tpp")
    @CommandOptions({"p", "l", "[arguments]"})
    @PermissionRestriction(TP_ALL_PERMISSION)
    public void teleportCommand(OptionSet set, CommandSender sender, Player[] player, Location location, List<Player[]> args)
    {
        Location teleportLoc = null;

        if(location != null) {
            teleportLoc = location;
        }

        if(player != null && player.length == 1) {
            teleportLoc = player[0].getLocation();
        }

        if(teleportLoc == null) {
            translate.sendMessage("teleport.provide_location", sender);
            return;
        }

        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);

        if(players.isEmpty()) {
            translate.sendMessage("supply one player name", sender);
            return;
        }

        for(Player p : players) {
            p.teleport(teleportLoc);
        }

        translate.sendMessage("teleport.all_teleported", sender, players.size());
    }

    @OptionsMethod
    public void teleportCommand(OptionDeclarer parser)
    {
        parser.accepts("p", "Player to teleport to")
                .withRequiredArg()
                .withValuesConvertedBy(new OnlinePlayerValueConverter(false));

        parser.accepts("l", "Location to teleport to")
                .withRequiredArg()
                .withValuesConvertedBy(new LocationValueConverter());

        parser.nonOptions("Player to teleport to, * for all players")
                .withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
