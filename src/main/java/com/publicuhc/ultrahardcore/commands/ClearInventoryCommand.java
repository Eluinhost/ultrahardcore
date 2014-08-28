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

import com.publicuhc.pluginframework.routing.annotation.*;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionDeclarer;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class ClearInventoryCommand extends TranslatingCommand {

    public static final String CLEAR_SELF_PERMISSION = "UHC.ci.self";
    public static final String CLEAR_OTHER_PERMISSION = "UHC.ci.other";
    public static final String CLEAR_IMMUNE_PERMISSION = "UHC.ci.immune";

    @Inject
    private ClearInventoryCommand(Translate translate) {
        super(translate);
    }

    @CommandMethod("ciself")
    @PermissionRestriction(CLEAR_SELF_PERMISSION)
    @SenderRestriction(Player.class)
    public void clearInventorySelf(OptionSet set, Player sender)
    {
        clearInventory(sender);
        sender.sendMessage(translate("ci.cleared", sender));
    }

    @CommandMethod("ci")
    @PermissionRestriction(CLEAR_OTHER_PERMISSION)
    @CommandOptions("[arguments]")
    public void clearInventoryCommand(OptionSet set, CommandSender sender, List<Player[]> args)
    {
        Set<Player> players = OnlinePlayerValueConverter.recombinePlayerLists(args);

        for(Player p : players) {
            if (p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                sender.sendMessage(translate("ci.immune", sender, "name", p.getName()));
            } else {
                clearInventory(p);
                p.sendMessage(translate("ci.tell", sender, "name", sender.getName()));
            }
        }

        sender.sendMessage(translate("ci.cleared", sender));
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
    private static void clearInventory(HumanEntity p) {
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
    }
}
