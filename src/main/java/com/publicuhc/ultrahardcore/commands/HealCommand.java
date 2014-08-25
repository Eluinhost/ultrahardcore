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

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.OptionsMethod;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionParser;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class HealCommand extends TranslatingCommand {

    public static final String HEAL_SELF_PERMISSION = "UHC.heal.self";
    public static final String HEAL_OTHER_PERMISSION = "UHC.heal.other";
    public static final String HEAL_ANNOUNCE_PERMISSION = "UHC.heal.announce";

    @Inject
    private HealCommand(Translate translate)
    {
        super(translate);
    }

    @CommandMethod(command = "healself", permission = HEAL_SELF_PERMISSION)
    public void healSelfCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        player.setHealth(player.getMaxHealth());
        player.sendMessage(translate("heal.tell", player));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("healer", player.getName());
        vars.put("name", player.getName());
        ServerUtil.broadcastForPermission(translate("heal.announce", player, vars),HEAL_ANNOUNCE_PERMISSION);
    }

    @CommandMethod(command = "heal", permission = HEAL_OTHER_PERMISSION, options = true)
    public void healCommand(CommandRequest request)
    {
        Iterable<Player[]> playersList = (Iterable<Player[]>) request.getOptions().nonOptionArguments();
        Collection<Player> players = new HashSet<Player>();
        for(Player[] comboPlayers : playersList) {
            Collections.addAll(players, comboPlayers);
        }

        for(Player p : players) {
            p.setHealth(p.getMaxHealth());
            p.sendMessage(translate("heal.tell", p));
            request.sendMessage(translate("heal.healed", p));
        }
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("healer", request.getSender().getName());
        vars.put("name", players.toString());
        ServerUtil.broadcastForPermission(translate("heal.announce", request.getSender(), vars), HEAL_ANNOUNCE_PERMISSION);
    }

    @OptionsMethod
    public void healCommand(OptionParser parser)
    {
        parser.nonOptions("Players to heal").withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
