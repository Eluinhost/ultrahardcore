/*
 * WhitelistCommands.java
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
