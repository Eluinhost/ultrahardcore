/*
 * HealCommand.java
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

import com.publicuhc.pluginframework.routing.annotation.*;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class HealCommand extends TranslatingCommand {

    public static final String HEAL_SELF_PERMISSION = "UHC.heal.self";
    public static final String HEAL_OTHER_PERMISSION = "UHC.heal.other";
    public static final String HEAL_ANNOUNCE_PERMISSION = "UHC.heal.announce";

    @Inject
    private HealCommand(Translate translate)
    {
        super(translate);
    }

    @CommandMethod("healself")
    @PermissionRestriction(HEAL_SELF_PERMISSION)
    @SenderRestriction(Player.class)
    public void healSelfCommand(OptionSet set, Player player){
        player.setHealth(player.getMaxHealth());
        player.sendMessage(translate("heal.tell", player));
        ServerUtil.broadcastForPermission(translate("heal.announce", player, player.getName(), player.getName()),HEAL_ANNOUNCE_PERMISSION);
    }

    @CommandMethod("heal")
    @PermissionRestriction(HEAL_OTHER_PERMISSION)
    @CommandOptions("[arguments]")
    public void healCommand(OptionSet set, CommandSender sender, List<Player[]> args)
    {
        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);

        for(Player p : players) {
            p.setHealth(p.getMaxHealth());
            p.sendMessage(translate("heal.tell", p));
            sender.sendMessage(translate("heal.healed", p));
        }

        ServerUtil.broadcastForPermission(translate("heal.announce", sender, sender.getName(), players.toString()), HEAL_ANNOUNCE_PERMISSION);
    }

    @OptionsMethod
    public void healCommand(OptionDeclarer parser)
    {
        parser.nonOptions("Players to heal").withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
