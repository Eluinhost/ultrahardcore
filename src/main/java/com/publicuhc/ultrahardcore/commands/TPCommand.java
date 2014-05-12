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

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class TPCommand extends SimpleCommand {

    public static final String TP_ALL_PERMISSION = "UHC.tpall";

    private final ScatterManager m_scatterManager;

    @Inject
    private TPCommand(Configurator configManager, Translate translate, ScatterManager scatterManager) {
        super(configManager, translate);
        m_scatterManager = scatterManager;
    }

    /**
     * Ran on /tpp {list of players} {player/location}
     * @param request request params
     */
    @CommandMethod
    public void teleportCommand(CommandRequest request){
        List<String> arguments = request.getArgs();
        Location location;
        String lastArg = request.getLastArg();
        if (lastArg.contains(",")) {
            String[] coords = lastArg.split(",");
            World w;
            if (coords.length == 3) {
                if (!(request.getSender() instanceof Player)) {
                    request.sendMessage(translate("teleport.non_player_world", request.getLocale()));
                    return;
                }
                w = ((Entity) request.getSender()).getWorld();
            } else if (coords.length == 4) {
                w = Bukkit.getWorld(coords[3]);
                if (w == null) {
                    request.sendMessage(translate("teleport.invalid.world", request.getLocale()));
                    return;
                }
            } else {
                request.sendMessage(translate("teleport.invalid.coords", request.getLocale()));
                return;
            }
            int x;
            int y;
            int z;
            try {
                x = Integer.parseInt(coords[0]);
                y = Integer.parseInt(coords[1]);
                z = Integer.parseInt(coords[2]);
            } catch (NumberFormatException ignored) {
                request.sendMessage(translate("teleport.invalid.coords", request.getLocale()));
                return;
            }
            location = new Location(w, x, y, z);
        } else {
            Player p = Bukkit.getPlayer(lastArg);
            if (p == null) {
                request.sendMessage(translate("teleport.invalid.player", request.getLocale(), "name", lastArg));
                return;
            }
            location = p.getLocation();
        }
        if (arguments.size() == 2 && "*".equals(request.getFirstArg())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                m_scatterManager.teleportSafe(p, location, false);
            }
        } else {
            for (int i = 0; i < arguments.size() - 1; i++) {
                Player p = Bukkit.getPlayer(arguments.get(i));
                if (p == null) {
                    request.sendMessage(translate("teleport.invalid.player", request.getLocale(), "name", arguments.get(i)));
                    continue;
                }
                m_scatterManager.teleportSafe(p, location, false);
            }
        }
        request.sendMessage(translate("teleport.all_teleported", request.getLocale()));
    }

    @RouteInfo
    public void teleportCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(TP_ALL_PERMISSION)
                .restrictArgumentCount(2, -1)
                .restrictCommand("tpp");
    }
}
