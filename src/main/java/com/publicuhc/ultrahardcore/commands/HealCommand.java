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

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routing.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HealCommand extends SimpleCommand {

    public static final String HEAL_SELF_PERMISSION = "UHC.heal.self";
    public static final String HEAL_OTHER_PERMISSION = "UHC.heal.other";
    public static final String HEAL_ANNOUNCE_PERMISSION = "UHC.heal.announce";

    @Inject
    private HealCommand(Configurator configManager, Translate translate) {
        super(configManager, translate);
    }

    /**
     * heal yourself
     * @param request request params
     */
    @CommandMethod
    public void healSelfCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        player.setHealth(player.getMaxHealth());
        player.sendMessage(translate("heal.tell", request.getLocale()));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("healer", player.getName());
        vars.put("name", player.getName());
        ServerUtil.broadcastForPermission(translate("heal.announce", request.getLocale(), vars),HEAL_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /healself.*
     */
    @RouteInfo
    public void healSelfCommandDetails(RouteBuilder builder) {
        builder.restrictSenderType(SenderType.PLAYER);
        builder.restrictCommand("healself");
        builder.restrictPermission(HEAL_SELF_PERMISSION);
    }

    /**
     * heal someone else
     * @param request request params
     */
    @CommandMethod
    public void healCommand(CommandRequest request){
        Player p = request.getPlayer(0);
        if (p == null) {
            request.sendMessage(translate("heal.invalid_player", request.getLocale(), "name", request.getFirstArg()));
            return;
        }
        p.setHealth(p.getMaxHealth());
        p.sendMessage(translate("heal.tell", request.getLocale()));
        request.sendMessage(translate("heal.healed", request.getLocale()));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("healer", request.getSender().getName());
        vars.put("name", p.getName());
        ServerUtil.broadcastForPermission(translate("heal.announce", request.getLocale(), vars), HEAL_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /heal .+ except '/heal *'
     */
    @RouteInfo
    public void healCommandDetails(RouteBuilder builder) {
        builder.restrictPattern(Pattern.compile("[^*]+"));
        builder.restrictPermission(HEAL_OTHER_PERMISSION);
        builder.restrictCommand("heal");
    }

    /**
     * Ran on /heal *
     * @param request request params
     */
    @CommandMethod
    public void healAllCommand(CommandRequest request){
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(p.getMaxHealth());
            p.sendMessage(translate("heal.tell", request.getLocale()));
        }
        request.sendMessage(translate("heal.heal_all", request.getLocale()));
        ServerUtil.broadcastForPermission(translate("heal.heal_all_announce", request.getLocale(), "name", request.getSender().getName()), HEAL_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /heal * only
     */
    @RouteInfo
    public void healAllCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("heal");
        builder.restrictPermission(HEAL_OTHER_PERMISSION);
        builder.restrictPattern(Pattern.compile("\\*"));
    }
}
