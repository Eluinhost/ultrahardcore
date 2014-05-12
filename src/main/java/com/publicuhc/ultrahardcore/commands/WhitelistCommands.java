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

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public class WhitelistCommands extends SimpleCommand {

    public static final String WHITELIST_ALL_PERMISSION = "UHC.whitelistall";

    /**
     * @param configManager the config manager
     * @param translate     the translator
     */
    @Inject
    protected WhitelistCommands(Configurator configManager, Translate translate) {
        super(configManager, translate);
    }

    @CommandMethod
    public void whitelistAllCommand(CommandRequest request) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.setWhitelisted(true);
        }
        request.sendMessage(translate("whitelist.added", request.getLocale()));
    }

    @RouteInfo
    public void whitelistAllCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(WHITELIST_ALL_PERMISSION)
                .restrictCommand("whitelistall")
                .maxMatches(1);
    }

    @CommandMethod
    public void whitelistClearCommand(CommandRequest request) {
        Set<OfflinePlayer> players = Bukkit.getWhitelistedPlayers();
        for(OfflinePlayer p : players) {
            p.setWhitelisted(false);
        }
        request.sendMessage(translate("whitelist.cleared", request.getLocale()));
    }

    @RouteInfo
    public void whitelistClearCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(WHITELIST_ALL_PERMISSION)
                .restrictStartsWith("clear")
                .restrictCommand("whitelistall");
    }
}
