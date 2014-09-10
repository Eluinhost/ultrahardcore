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

import com.google.common.base.Joiner;
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
import com.publicuhc.ultrahardcore.api.Command;
import com.publicuhc.ultrahardcore.api.Feature;
import com.publicuhc.ultrahardcore.api.FeatureManager;
import com.publicuhc.ultrahardcore.features.PlayerFreezeFeature;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class FreezeCommand implements Command
{

    public static final String FREEZE_PERMISSION = "UHC.freeze.command";
    public static final String ANTIFREEZE_PERMISSION = "UHC.freeze.antifreeze";

    private final FeatureManager featureManager;
    private final Translate translate;

    @Inject
    private FreezeCommand(FeatureManager features, Translate translate) {
        this.translate = translate;
        featureManager = features;
    }

    private boolean isValid(Optional<Feature> featureOptional, CommandSender sender)
    {
        if (!featureOptional.isPresent()) {
            translate.sendMessage("freeze.not_loaded", sender);
            return false;
        }

        if (!featureOptional.get().isEnabled()) {
            translate.sendMessage("freeze.not_enabled", sender);
            return false;
        }

        return true;
    }

    @CommandMethod("freeze")
    @PermissionRestriction(FREEZE_PERMISSION)
    @CommandOptions("[arguments]")
    public void freezeCommand(OptionSet set, CommandSender sender, List<Player[]> args)
    {
        Optional<Feature> featureOptional = featureManager.getFeatureByID("PlayerFreeze");

        if(!isValid(featureOptional, sender)) {
            return;
        }

        PlayerFreezeFeature feature = (PlayerFreezeFeature) featureOptional.get();

        if(set.has("a")) {
            feature.freezeAll();
            translate.broadcastMessage("freeze.all_frozen");
            return;
        }

        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);

        if(players.isEmpty()) {
            translate.sendMessage("supply one player name", sender);
            return;
        }

        Collection<String> immune = new ArrayList<String>();
        for(Player player : players) {
            if (player.hasPermission(ANTIFREEZE_PERMISSION)) {
                immune.add(player.getName());
                continue;
            }
            feature.addPlayer(player);
            translate.sendMessage("freeze.freeze_tell", player);
        }

        if(!immune.isEmpty()) {
            translate.sendMessage("freeze.immune", sender, Joiner.on(", ").join(immune));
        }

        int count = players.size() - immune.size();
        translate.sendMessage("freeze.frozen", sender, count);
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
    public void unfreezeCommand(OptionSet set, CommandSender sender, List<Player[]> args)
    {
        Optional<Feature> featureOptional = featureManager.getFeatureByID("PlayerFreeze");
        if(!isValid(featureOptional, sender)) {
            return;
        }

        PlayerFreezeFeature feature = (PlayerFreezeFeature) featureOptional.get();

        if(set.has("a")) {
            feature.unfreezeAll();
            translate.broadcastMessage("freeze.all_unfrozen", sender);
            return;
        }

        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);

        if(players.isEmpty()) {
            translate.sendMessage("supply one player name", sender);
            return;
        }

        for(Player player : players) {
            feature.removePlayer(player);
            translate.sendMessage("freeze.unfreeze_tell", player);
        }

        translate.sendMessage("freeze.unfrozen", sender, players.size());
    }

    @OptionsMethod
    public void unfreezeCommand(OptionDeclarer parser)
    {
        parser.accepts("a", "Unfreeze all players");
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }
}
