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

package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.OptionsMethod;
import com.publicuhc.pluginframework.routing.converters.LocationValueConverter;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionParser;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class TPCommand extends TranslatingCommand {

    public static final String TP_ALL_PERMISSION = "UHC.tpall";

    @Inject
    private TPCommand(Translate translate) {
        super(translate);
    }

    /**
     * Ran on /tpp {list of players} {player/location}
     * @param request request params
     */
    @CommandMethod(command = "tpp", options = true, permission = TP_ALL_PERMISSION)
    public void teleportCommand(CommandRequest request){
        OptionSet set = request.getOptions();

        Location teleportLoc = set.has("p") ? ((Player) set.valueOf("p")).getLocation() : (Location) set.valueOf("l");
        Iterable<Player[]> playersList = (Iterable<Player[]>) request.getOptions().nonOptionArguments();
        Collection<Player> players = new HashSet<Player>();
        for(Player[] comboPlayers : playersList) {
            Collections.addAll(players, comboPlayers);
        }
        for (Player p : players) {
            p.teleport(teleportLoc);
        }

        request.sendMessage(translate("teleport.all_teleported", request.getSender()));
    }

    @OptionsMethod
    public void teleportCommandDetails(OptionParser parser) {
        parser.accepts("p", "Player to teleport to")
                .requiredUnless("l")
                .withRequiredArg()
                .withValuesConvertedBy(new OnlinePlayerValueConverter(false));
        parser.accepts("l", "Location to teleport to")
                .requiredUnless("p")
                .withRequiredArg()
                .withValuesConvertedBy(new LocationValueConverter());
        parser.nonOptions("Player to teleport to, * for all players")
                .withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
