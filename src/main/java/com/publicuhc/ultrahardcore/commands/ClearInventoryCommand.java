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

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.OptionsMethod;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionParser;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ClearInventoryCommand extends Command {

    public static final String CLEAR_SELF_PERMISSION = "UHC.ci.self";
    public static final String CLEAR_OTHER_PERMISSION = "UHC.ci.other";
    public static final String CLEAR_IMMUNE_PERMISSION = "UHC.ci.immune";

    @Inject
    private ClearInventoryCommand(Translate translate) {
        super(translate);
    }

    /**
     * Ran on /ciself
     * @param request request params
     */
    @CommandMethod(command = "ciself", permission = CLEAR_SELF_PERMISSION)
    public void clearInventorySelf(CommandRequest request)
    {
        clearInventory((HumanEntity) request.getSender());
        request.sendMessage(translate("ci.cleared", request.getSender()));
    }

    @CommandMethod(command = "ci", permission = CLEAR_OTHER_PERMISSION)
    public void clearInventoryCommand(CommandRequest request)
    {
        List<Player> players = (List<Player>) request.getOptions().nonOptionArguments();

        for(Player p : players) {
            if (p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                request.sendMessage(translate("ci.immune", request.getSender(), "name", p.getName()));
            } else {
                clearInventory(p);
                p.sendMessage(translate("ci.tell", request.getSender(), "name", request.getSender().getName()));
            }
        }
        request.sendMessage(translate("ci.cleared", request.getSender()));
    }

    @OptionsMethod
    public void clearInventoryCommand(OptionParser parser)
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
