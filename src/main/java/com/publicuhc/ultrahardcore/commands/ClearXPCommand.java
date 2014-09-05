/*
 * ClearXPCommand.java
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
    private ClearXPCommand(Translate translate) {
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

        Collection<String> immune = new ArrayList<String>();

        for(Player p : players) {
            if (p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
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
     * @param p player
     */
    private void clearXP(Player p) {
        p.setTotalExperience(0);
        translate.sendMessage("cxp.tell", p);
    }
}
