/*
 * DeathBanCommand.java
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
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.deathbans.DeathBansFeature;
import com.publicuhc.ultrahardcore.util.WordsUtil;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathBanCommand extends SimpleCommand {

    public static final String DEATH_BAN_BAN = "UHC.deathban.unban";
    public static final String DEATH_BAN_UNBAN = "UHC.deathban.ban";

    private final FeatureManager m_features;

    /**
     * @param configManager the config manager
     * @param translate the translator
     * @param features the feature manager
     */
    @Inject
    private DeathBanCommand(Configurator configManager, Translate translate, FeatureManager features) {
        super(configManager, translate);
        m_features = features;
    }


    /**
     * Ran on /deathban
     * @param request the request params
     */
    @CommandMethod
    public void deathBanCommand(CommandRequest request){
        request.sendMessage(ChatColor.RED+"Syntax: /deathban unban <name>");
        request.sendMessage(ChatColor.RED+"Syntax: /deathban ban <name> <time>");
    }

    /**
     * Run on /deathban.*
     * @param builder the builder
     */
    @RouteInfo
    public void deathBanCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("deathban")
                .restrictPermission(DEATH_BAN_BAN)
                .maxMatches(1);
    }

    /**
     * Unban a player
     * @param request the request params
     */
    @CommandMethod
    public void deathbanUnbanCommand(CommandRequest request){
        IFeature feature = m_features.getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(translate("deathbans.not_loaded", request.getLocale()));
            return;
        }
        UUID playerID = request.getPlayerUUID(1);
        if(playerID.equals(CommandRequest.INVALID_ID)) {
            request.sendMessage(translate("deathban.uuid_error", request.getLocale()));
            return;
        }
        int amount = ((DeathBansFeature)feature).removeBan(playerID);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("amount", String.valueOf(amount));
        vars.put("name", request.getFirstArg());
        request.sendMessage(translate("deathbans.removed", request.getLocale(), vars));
    }

    /**
     * Ran on /deathban unban {player}
     * @param builder the builder
     */
    @RouteInfo
    public void deathbanUnbanCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(DEATH_BAN_UNBAN)
                .restrictCommand("deathban")
                .restrictStartsWith("unban")
                .restrictArgumentCount(2, 2);
    }

    /**
     * ban a player
     * @param request the request params
     */
    @CommandMethod
    public void deathbanBanCommand(CommandRequest request){
        IFeature feature = m_features.getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(translate("deathbans.not_loaded", request.getLocale()));
            return;
        }
        UUID playerID = request.getPlayerUUID(1);
        if(playerID.equals(CommandRequest.INVALID_ID)) {
            request.sendMessage(translate("deathban.uuid_error", request.getLocale()));
            return;
        }
        long duration = WordsUtil.parseTime(request.getArg(2));
        ((DeathBansFeature)feature).banPlayer(playerID, translate("deathbans.ban_message", request.getLocale()), duration);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("name", request.getArg(1));
        vars.put("time", WordsUtil.formatTimeLeft(System.currentTimeMillis() + duration));
        request.sendMessage(translate("deathbans.banned", request.getLocale(), vars));
    }

    /**
     * Ran on /deathban ban {player} {time}
     * @param builder the builder
     */
    @RouteInfo
    public void deathbanBanCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("deathban")
                .restrictPermission(DEATH_BAN_BAN)
                .restrictStartsWith("ban")
                .restrictArgumentCount(2, 2);
    }
}
