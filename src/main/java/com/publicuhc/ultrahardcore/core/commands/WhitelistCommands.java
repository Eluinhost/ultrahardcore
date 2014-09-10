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

package com.publicuhc.ultrahardcore.core.commands;

import com.publicuhc.pluginframework.routing.annotation.CommandMethod;
import com.publicuhc.pluginframework.routing.annotation.CommandOptions;
import com.publicuhc.pluginframework.routing.annotation.OptionsMethod;
import com.publicuhc.pluginframework.routing.annotation.PermissionRestriction;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class WhitelistCommands implements Command
{

    public static final String WHITELIST_ALL_PERMISSION = "UHC.whitelistall";

    private final Translate translate;

    @Inject
    protected WhitelistCommands(Translate translate)
    {
        this.translate = translate;
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
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.setWhitelisted(true);
        }
    }

    @CommandMethod("whitelistall")
    @PermissionRestriction(WHITELIST_ALL_PERMISSION)
    @CommandOptions
    public void whitelistAllCommand(OptionSet set, CommandSender sender)
    {
        if(set.has("c")) {
            clearWhitelist();
            translate.sendMessage("whitelist.cleared", sender);
        } else {
            addAllToWhitelist();
            translate.sendMessage("whitelist.added", sender);
        }
    }

    @OptionsMethod
    public void whitelistAllCommand(OptionDeclarer parser)
    {
        parser.accepts("c", "Clears the whitelist instead");
    }
}
