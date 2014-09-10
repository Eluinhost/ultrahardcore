/*
 * ClearXPCommand.java
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

import com.google.common.base.Joiner;
import com.publicuhc.pluginframework.routing.annotation.*;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ClearXPCommand implements Command
{

    public static final String CLEAR_SELF_PERMISSION = "UHC.cxp.self";
    public static final String CLEAR_OTHER_PERMISSION = "UHC.cxp.other";
    public static final String CLEAR_IMMUNE_PERMISSION = "UHC.cxp.immune";
    public static final String CLEAR_ANNOUNCE_PERMISSION = "UHC.cxp.announce";

    private final Translate translate;

    @Inject
    private ClearXPCommand(Translate translate)
    {
        this.translate = translate;
    }

    @CommandMethod("cxpself")
    @PermissionRestriction(CLEAR_SELF_PERMISSION)
    @SenderRestriction(Player.class)
    public void clearInventorySelf(OptionSet set, Player sender)
    {
        clearXP(sender);
        translate.broadcastMessageForPermission(CLEAR_ANNOUNCE_PERMISSION, "cxp.announce_self", sender.getName());
    }

    @CommandMethod("cxp")
    @PermissionRestriction(CLEAR_OTHER_PERMISSION)
    @CommandOptions("[arguments]")
    public void clearXPCommand(OptionSet set, CommandSender sender, List<Player[]> args)
    {
        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);


        if(players.isEmpty()) {
            translate.sendMessage("supply one player name", sender);
            return;
        }

        Collection<String> immune = new ArrayList<String>();
        for(Player p : players) {
            if(p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                immune.add(p.getName());
            } else {
                clearXP(p);
            }
        }

        if(!immune.isEmpty()) {
            String playerList = Joiner.on(", ").join(immune);
            translate.sendMessage("cxp.immune", sender, playerList);
        }

        int cleared = players.size() - immune.size();

        translate.sendMessage("cxp.cleared", sender, cleared);
        translate.broadcastMessageForPermission(CLEAR_ANNOUNCE_PERMISSION, "cxp.announce", sender.getName(), cleared);
    }

    @OptionsMethod
    public void clearXPCommand(OptionDeclarer parser)
    {
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }

    /**
     * Clears the player's xp
     *
     * @param p player
     */
    private void clearXP(Player p)
    {
        p.setTotalExperience(0);
        translate.sendMessage("cxp.tell", p);
    }
}
