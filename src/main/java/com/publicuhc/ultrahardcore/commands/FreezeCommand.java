/*
 * FreezeCommand.java
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
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.playerfreeze.PlayerFreezeFeature;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FreezeCommand extends SimpleCommand {

    public static final String FREEZE_PERMISSION = "UHC.freeze.command";
    public static final String ANTIFREEZE_PERMISSION = "UHC.freeze.antifreeze";

    private final FeatureManager m_features;

    /**
     * The freeze command
     * @param features the feature manager
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private FreezeCommand(FeatureManager features, Configurator configManager, Translate translate) {
        super(configManager, translate);
        m_features = features;
    }

    /**
     * Ran on /freeze
     * @param request the request params
     */
    @CommandMethod
    public void freezeCommand(CommandRequest request){
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded", request.getLocale()));
            return;
        }
        if(!feature.isEnabled()) {
            request.sendMessage(translate("freeze.not_enabled", request.getLocale()));
            return;
        }
        Player player = request.getPlayer(0);
        if(player == null){
            request.sendMessage(translate("freeze.invalid_player", request.getLocale(), "name", request.getFirstArg()));
            return;
        }
        if(player.hasPermission(ANTIFREEZE_PERMISSION)){
            request.sendMessage(translate("freeze.immune", request.getLocale()));
            return;
        }
        ((PlayerFreezeFeature)feature).addPlayer(player);
        request.sendMessage(translate("freeze.player_froze", request.getLocale()));
    }

    @RouteInfo
    public void freezeCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("freeze")
                .restrictArgumentCount(1, 1)
                .restrictPermission(FREEZE_PERMISSION)
                .maxMatches(1);
    }

    /**
     * Ran on /freeze *
     * @param request the request params
     */
    @CommandMethod
    public void freezeAllCommand(CommandRequest request){
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded", request.getLocale()));
            return;
        }
        if(!feature.isEnabled()) {
            request.sendMessage(translate("freeze.not_enabled", request.getLocale()));
            return;
        }
        ((PlayerFreezeFeature)feature).freezeAll();
        request.sendMessage(translate("freeze.froze_all", request.getLocale()));
    }

    @RouteInfo
    public void freezeAllCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(FREEZE_PERMISSION)
                .restrictCommand("freeze")
                .restrictStartsWith("*");
    }

    /**
     * Ran on /unfreeze
     * @param request the request params
     */
    @CommandMethod
    public void unfreezeCommand(CommandRequest request){
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded", request.getLocale()));
            return;
        }
        if(!feature.isEnabled()) {
            request.sendMessage(translate("freeze.not_enabled", request.getLocale()));
            return;
        }
        Player p = request.getPlayer(0);
        if(null == p) {
            request.sendMessage(translate("freeze.invalid_player", request.getLocale(), "name", request.getFirstArg()));
            return;
        }
        ((PlayerFreezeFeature)feature).removePlayer(p.getUniqueId());
        request.sendMessage(translate("freeze.player_unfroze", request.getLocale()));
    }

    @RouteInfo
    public void unfreezeCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("unfreeze")
                .restrictArgumentCount(1, 1)
                .maxMatches(1).
                restrictPermission(FREEZE_PERMISSION);
    }

    /**
     * Ran on /unfreeze *
     * @param request the request params
     */
    @CommandMethod
    public void unfreezeAllCommand(CommandRequest request){
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded", request.getLocale()));
            return;
        }
        if(!feature.isEnabled()) {
            request.sendMessage(translate("freeze.not_enabled", request.getLocale()));
            return;
        }
        ((PlayerFreezeFeature)feature).unfreezeAll();
        request.sendMessage(translate("freeze.unfroze_all", request.getLocale()));
    }

    @RouteInfo
    public void unfreezeAllCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(FREEZE_PERMISSION)
                .restrictStartsWith("*")
                .restrictCommand("unfreeze");
    }

    @CommandMethod
    public void toggleFreezeCommand(CommandRequest request) {
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded", request.getLocale()));
            return;
        }
        if(!feature.isEnabled()) {
            request.sendMessage(translate("freeze.not_enabled", request.getLocale()));
            return;
        }
        PlayerFreezeFeature freezeFeature = (PlayerFreezeFeature) feature;
        Player player = request.getPlayer(1);

        if(null == player) {
            request.sendMessage(translate("freeze.invalid_player", request.getLocale(), "name", request.getArg(1)));
            return;
        }

        if(freezeFeature.isPlayerFrozen(player)) {
            freezeFeature.addPlayer(player);
            request.sendMessage(translate("freeze.player_froze", request.getLocale()));
        } else {
            freezeFeature.removePlayer(player);
            request.sendMessage(translate("freeze.player_unfroze", request.getLocale()));
        }
    }

    @RouteInfo
    public void toggleFreezeCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("freeze")
                .restrictPermission(FREEZE_PERMISSION)
                .restrictArgumentCount(2,2)
                .restrictStartsWith("toggle")
                .maxMatches(2);
    }

    @CommandMethod
    public void toggleFreezeAllCommand(CommandRequest request) {
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded", request.getLocale()));
            return;
        }
        if(!feature.isEnabled()) {
            request.sendMessage(translate("freeze.not_enabled", request.getLocale()));
            return;
        }
        PlayerFreezeFeature freezeFeature = (PlayerFreezeFeature) feature;

        if(freezeFeature.isGlobalMode()) {
            freezeFeature.unfreezeAll();
            request.sendMessage(translate("freeze.unfroze_all", request.getLocale()));
        } else {
            freezeFeature.freezeAll();
            request.sendMessage(translate("freeze.froze_all", request.getLocale()));
        }
    }

    @CommandMethod
    public void toggleFreezeAllCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("freeze")
                .restrictStartsWith("toggle *")
                .restrictPermission(FREEZE_PERMISSION);
    }
}
