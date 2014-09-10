/*
 * ClearInventoryCommand.java
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
import com.publicuhc.pluginframework.routing.annotation.*;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Command;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ClearInventoryCommand implements Command
{

    public static final String CLEAR_SELF_PERMISSION = "UHC.ci.self";
    public static final String CLEAR_OTHER_PERMISSION = "UHC.ci.other";
    public static final String CLEAR_IMMUNE_PERMISSION = "UHC.ci.immune";
    public static final String CLEAR_ANNOUNCE_PERMISSION = "UHC.ci.announce";

    private final Translate translate;

    @Inject
    private ClearInventoryCommand(Translate translate) {
        this.translate = translate;
    }

    @CommandMethod("ciself")
    @PermissionRestriction(CLEAR_SELF_PERMISSION)
    @SenderRestriction(Player.class)
    public void clearInventorySelf(OptionSet set, Player sender)
    {
        clearInventory(sender);
        translate.broadcastMessageForPermission(CLEAR_ANNOUNCE_PERMISSION, "ci.announce_self", sender.getName());
    }

    @CommandMethod("ci")
    @PermissionRestriction(CLEAR_OTHER_PERMISSION)
    @CommandOptions("[arguments]")
    public void clearInventoryCommand(OptionSet set, CommandSender sender, List<Player[]> args)
    {
        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);


        if(players.isEmpty()) {
            translate.sendMessage("supply one player name", sender);
            return;
        }

        Collection<String> immune = new ArrayList<String>();
        for(Player p : players) {
            if (p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                immune.add(p.getName());
            } else {
                clearInventory(p);
            }
        }

        if(!immune.isEmpty()) {
            String playerList = Joiner.on(", ").join(immune);
            translate.sendMessage("ci.immune", sender, playerList);
        }

        int cleared = players.size() - immune.size();

        translate.sendMessage("ci.cleared", sender, cleared);
        translate.broadcastMessageForPermission(CLEAR_ANNOUNCE_PERMISSION, "ci.announce", sender.getName(), cleared);
    }

    @OptionsMethod
    public void clearInventoryCommand(OptionDeclarer parser)
    {
        parser.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
    }

    /**
     * Clears the player's inventory, armour slots, item on cursor and crafting slots
     * @param p player
     */
    private void clearInventory(Player p) {
        p.getInventory().clear();
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.setItemOnCursor(new ItemStack(Material.AIR));
        InventoryView openInventory = p.getOpenInventory();
        if (openInventory.getType() == InventoryType.CRAFTING) {
            openInventory.getTopInventory().clear();
        }

        translate.sendMessage("ci.tell", p);
    }
}
