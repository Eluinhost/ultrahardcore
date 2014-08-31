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

import com.google.common.base.Optional;
import com.publicuhc.pluginframework.routing.annotation.CommandMethod;
import com.publicuhc.pluginframework.routing.annotation.CommandOptions;
import com.publicuhc.pluginframework.routing.annotation.OptionsMethod;
import com.publicuhc.pluginframework.routing.annotation.PermissionRestriction;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Feature;
import com.publicuhc.ultrahardcore.api.FeatureManager;
import com.publicuhc.ultrahardcore.features.PlayerFreezeFeature;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class FreezeCommand extends TranslatingCommand {

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

    @CommandMethod("freeze")
    @PermissionRestriction(FREEZE_PERMISSION)
    @CommandOptions("[arguments]")
    public void freezeCommand(OptionSet set, CommandSender sender, List<Player[]> args) {
        Optional<Feature> featureOptional = featureManager.getFeatureByID("PlayerFreeze");
        if (!featureOptional.isPresent()) {
            sender.sendMessage(translate("freeze.not_loaded", sender));
            return;
        }
        PlayerFreezeFeature feature = (PlayerFreezeFeature) featureOptional.get();
        if (!feature.isEnabled()) {
            sender.sendMessage(translate("freeze.not_enabled", sender));
            return;
        }

        if(set.has("a")) {
            feature.freezeAll();
            sender.sendMessage(translate("freeze.froze_all", sender));
            return;
        }

        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);
        for(Player player : players) {
            if (player.hasPermission(ANTIFREEZE_PERMISSION)) {
                sender.sendMessage(translate("freeze.immune", sender));
                return;
            }
            feature.addPlayer(player);
        }
        sender.sendMessage(translate("freeze.player_froze", sender));
    }

    @OptionsMethod
    public void freezeCommand(OptionDeclarer parser)
    {
        parser.accepts("a", "Freeze all online players and any new connections");
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }

    @CommandMethod("unfreeze")
    @PermissionRestriction(FREEZE_PERMISSION)
    @CommandOptions("[arguments]")
    public void unfreezeCommand(OptionSet set, CommandSender sender, List<Player[]> args) {
        Optional<Feature> featureOptional = featureManager.getFeatureByID("PlayerFreeze");
        if (!featureOptional.isPresent()) {
            sender.sendMessage(translate("freeze.not_loaded", sender));
            return;
        }
        PlayerFreezeFeature feature = (PlayerFreezeFeature) featureOptional.get();
        if (!feature.isEnabled()) {
            sender.sendMessage(translate("freeze.not_enabled", sender));
            return;
        }

        if(set.has("a")) {
            feature.unfreezeAll();
            sender.sendMessage(translate("freeze.unfroze_all", sender));
            return;
        }

        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);
        for(Player player : players) {
            feature.removePlayer(player);
        }
        sender.sendMessage(translate("freeze.player_unfroze", sender));
    }

    @OptionsMethod
    public void unfreezeCommand(OptionDeclarer parser)
    {
        parser.accepts("a", "Unfreeze all players");
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
