/*
 * WhitelistCommands.java
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
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionParser;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public class WhitelistCommands extends TranslatingCommand {

    public static final String WHITELIST_ALL_PERMISSION = "UHC.whitelistall";

    @Inject
    protected WhitelistCommands(Translate translate) {
        super(translate);
    }

    @CommandMethod(command = "whitelistall", permission = WHITELIST_ALL_PERMISSION, options = true)
    public void whitelistAllCommand(CommandRequest request)
    {
        if(request.getOptions().has("c")) {
            clearWhitelist();
            request.sendMessage(translate("whitelist.cleared", request.getSender()));
        } else {
            addAllToWhitelist();
            request.sendMessage(translate("whitelist.added", request.getSender()));
        }
    }

    @OptionsMethod
    public void whitelistAllCommand(OptionParser parser)
    {
        parser.accepts("c", "Clears the whitelist instead");
    }

    private static void clearWhitelist()
    {
        Set<OfflinePlayer> players = Bukkit.getWhitelistedPlayers();
        for(OfflinePlayer p : players) {
            p.setWhitelisted(false);
        }
    }

    private static void addAllToWhitelist()
    {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setWhitelisted(true);
        }
    }
}
