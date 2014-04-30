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

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class ClearInventoryCommand extends SimpleCommand {

    public static final String CLEAR_SELF_PERMISSION = "UHC.ci.self";
    public static final String CLEAR_OTHER_PERMISSION = "UHC.ci.other";
    public static final String CLEAR_IMMUNE_PERMISSION = "UHC.ci.immune";

    /**
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private ClearInventoryCommand(Configurator configManager, Translate translate) {
        super(configManager, translate);
    }

    /**
     * Ran on /ciself
     * @param request request params
     */
    @CommandMethod
    public void clearInventorySelf(CommandRequest request){
        clearInventory((HumanEntity) request.getSender());
        request.sendMessage(translate("ci.cleared", request.getLocale()));
    }

    /**
     * Run on /ciself
     *
     * @param builder the builder
     */
    @RouteInfo
    public void clearInventorySelfDetails(RouteBuilder builder) {
        builder.restrictCommand("ciself")
                .restrictPermission(CLEAR_SELF_PERMISSION)
                .restrictSenderType(SenderType.PLAYER);
    }

    /**
     * Ran on /ci {name}*
     * @param request request params
     */
    @CommandMethod
    public void clearInventoryCommand(CommandRequest request){
        List<String> arguments = request.getArgs();
        int amount = request.getArgs().size();
        AbstractList<String> namesNotFound = new ArrayList<String>();
        for (int i = 0; i < amount; i++) {
            Player p = request.getPlayer(i);
            if (p == null) {
                namesNotFound.add(p.getName());
                continue;
            }
            if (p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                request.sendMessage(translate("ci.immune", request.getLocale(), "name", p.getName()));
            } else {
                clearInventory(p);
                p.sendMessage(translate("ci.tell", request.getLocale(), "name", request.getSender().getName()));
            }
        }
        request.sendMessage("ci.cleared");
        if (!namesNotFound.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String s : namesNotFound) {
                message.append(' ').append(s);
            }
            request.sendMessage(translate("ci.not_found", request.getLocale(), "list", message.toString()));
        }
    }

    /**
     * Run on /ci .* except /ci *
     * @param builder the builder
     */
    @RouteInfo
    public void clearInventoryCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(CLEAR_OTHER_PERMISSION)
                .restrictArgumentCount(1, -1)
                .restrictCommand("ci")
                .maxMatches(1);
    }

    /**
     * Ran on /ci *
     * @param request request params
     */
    @CommandMethod
    public void clearInventoryAll(CommandRequest request){
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                clearInventory(p);
            }
        }
        Bukkit.broadcastMessage(translate("ci.announce_all", request.getLocale(), "name", request.getSender().getName()));
    }

    /**
     * Run on /ci *
     * @param builder the builder
     */
    @RouteInfo
    public void clearInventoryAll(RouteBuilder builder) {
        builder.restrictCommand("ci")
                .restrictPermission(CLEAR_OTHER_PERMISSION)
                .restrictStartsWith("*");
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
