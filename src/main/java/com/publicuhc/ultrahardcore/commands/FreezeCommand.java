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

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.OptionsMethod;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionParser;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.FeatureManager;
import com.publicuhc.ultrahardcore.features.PlayerFreezeFeature;
import org.bukkit.entity.Player;

import java.util.List;

public class FreezeCommand extends Command {

    public static final String FREEZE_PERMISSION = "UHC.freeze.command";
    public static final String ANTIFREEZE_PERMISSION = "UHC.freeze.antifreeze";

    private final FeatureManager featureManager;

    /**
     * The freeze command
     *
     * @param features      the feature manager
     * @param translate     the translator
     */
    @Inject
    private FreezeCommand(FeatureManager features, Translate translate) {
        super(translate);
        featureManager = features;
    }

    @CommandMethod(command = "freeze", permission = FREEZE_PERMISSION, options = true)
    public void freezeCommand(CommandRequest request) {
        OptionSet set = request.getOptions();

        PlayerFreezeFeature feature = (PlayerFreezeFeature) featureManager.getFeatureByID("PlayerFreeze");
        if (feature == null) {
            request.sendMessage(translate("freeze.not_loaded", request.getSender()));
            return;
        }
        if (!feature.isEnabled()) {
            request.sendMessage(translate("freeze.not_enabled", request.getSender()));
            return;
        }

        if(set.has("a")) {
            feature.freezeAll();
            request.sendMessage(translate("freeze.froze_all", request.getSender()));
            return;
        }

        List<Player> players = (List<Player>) request.getOptions().nonOptionArguments();

        for(Player player : players) {
            if (player.hasPermission(ANTIFREEZE_PERMISSION)) {
                request.sendMessage(translate("freeze.immune", request.getSender()));
                return;
            }
            feature.addPlayer(player);
        }
        request.sendMessage(translate("freeze.player_froze", request.getSender()));
    }

    @OptionsMethod
    public void freezeCommand(OptionParser parser)
    {
        parser.accepts("a", "Freeze all online players and any new connections");
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }

    @CommandMethod(command = "unfreeze", permission = FREEZE_PERMISSION, options = true)
    public void unfreezeCommand(CommandRequest request) {
        OptionSet set = request.getOptions();

        PlayerFreezeFeature feature = (PlayerFreezeFeature) featureManager.getFeatureByID("PlayerFreeze");
        if (feature == null) {
            request.sendMessage(translate("freeze.not_loaded", request.getSender()));
            return;
        }
        if (!feature.isEnabled()) {
            request.sendMessage(translate("freeze.not_enabled", request.getSender()));
            return;
        }

        if(set.has("a")) {
            feature.unfreezeAll();
            request.sendMessage(translate("freeze.unfroze_all", request.getSender()));
            return;
        }

        List<Player> players = (List<Player>) request.getOptions().nonOptionArguments();

        for(Player player : players) {
            feature.removePlayer(player);
        }
        request.sendMessage(translate("freeze.player_unfroze", request.getSender()));
    }

    @OptionsMethod
    public void unfreezeCommand(OptionParser parser)
    {
        parser.accepts("a", "Unfreeze all players");
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
