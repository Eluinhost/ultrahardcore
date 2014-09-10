/*
 * TPCommand.java
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
