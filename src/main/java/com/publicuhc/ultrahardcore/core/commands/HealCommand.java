/*
 * HealCommand.java
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

import com.publicuhc.pluginframework.routing.annotation.*;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class HealCommand implements Command
{

    public static final String HEAL_SELF_PERMISSION = "UHC.heal.self";
    public static final String HEAL_OTHER_PERMISSION = "UHC.heal.other";
    public static final String HEAL_ANNOUNCE_PERMISSION = "UHC.heal.announce";

    private final Translate translate;

    @Inject
    private HealCommand(Translate translate)
    {
        this.translate = translate;
    }

    private void healPlayer(Player player)
    {
        player.setHealth(player.getMaxHealth());
        translate.sendMessage("heal.tell", player);
    }

    @CommandMethod("healself")
    @PermissionRestriction(HEAL_SELF_PERMISSION)
    @SenderRestriction(Player.class)
    public void healSelfCommand(OptionSet set, Player player)
    {
        healPlayer(player);
        translate.broadcastMessageForPermission(HEAL_ANNOUNCE_PERMISSION, "heal.announce_self", player.getName());
    }

    @CommandMethod("heal")
    @PermissionRestriction(HEAL_OTHER_PERMISSION)
    @CommandOptions("[arguments]")
    public void healCommand(OptionSet set, CommandSender sender, List<Player[]> args)
    {
        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);

        if(players.isEmpty()) {
            translate.sendMessage("supply one player name", sender);
            return;
        }

        for(Player p : players) {
            healPlayer(p);
        }

        translate.sendMessage("heal.healed", sender, players.size());
        translate.broadcastMessageForPermission(HEAL_ANNOUNCE_PERMISSION, "heal.announce", sender.getName(), players.size());
    }

    @OptionsMethod
    public void healCommand(OptionDeclarer parser)
    {
        parser.nonOptions("Players to heal").withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
