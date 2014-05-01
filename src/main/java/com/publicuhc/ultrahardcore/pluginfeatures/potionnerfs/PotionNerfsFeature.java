/*
 * PotionNerfsFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.potionnerfs;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@Singleton
public class PotionNerfsFeature extends UHCFeature {

    public static final String POTION_BASE = BASE_PERMISSION + "potions.";
    public static final String DENY_SPLASH = POTION_BASE + "disableSplash";
    public static final String DENY_IMPROVED = POTION_BASE + "disableImproved";

    /**
     * Disallows tier 2 + splash when enabled, normal when disabled
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private PotionNerfsFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * Runs on a inventory click
     * @param ice InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent ice) {
        //if we're enabled
        if (isEnabled()) {
            FileConfiguration config = getConfigManager().getConfig("main");

            //if it's not a brewing stand skip
            if (ice.getInventory().getType() != InventoryType.BREWING) {
                return;
            }

            InventoryView iv = ice.getView();
            boolean cancel = false;

            boolean disableSplash = config.getBoolean(getBaseConfig()+"disableSplash");
            boolean disableGlowstone = config.getBoolean(getBaseConfig()+"disableGlowstone");

            //if the player is shift clicking
            if (ice.isShiftClick()) {
                //if splash disabled and they're clicking sulphur and don't have permission
                if (disableSplash && ice.getCurrentItem().getType() == Material.SULPHUR && ice.getWhoClicked().hasPermission(DENY_SPLASH)) {
                    cancel = true;
                }
                //if tier 2 is disabled and they're clicking glowstone and don't have permission
                if (disableGlowstone && ice.getCurrentItem().getType() == Material.GLOWSTONE_DUST && ice.getWhoClicked().hasPermission(DENY_IMPROVED)) {
                    cancel = true;
                }
            }

            //if its the fuel slot that was clicked
            if (ice.getSlotType() == InventoryType.SlotType.FUEL) {
                //if splash disabled and sulphur is on the cursor and no permission
                if (disableSplash && iv.getCursor().getType() == Material.SULPHUR && ice.getWhoClicked().hasPermission(DENY_SPLASH)) {
                    cancel = true;
                }
                //if tier 2 disabled and glowstone is on the cursor and no permission
                if (disableGlowstone && iv.getCursor().getType() == Material.GLOWSTONE_DUST && ice.getWhoClicked().hasPermission(DENY_IMPROVED)) {
                    cancel = true;
                }
            }

            //if they didn't have permission for action
            if (cancel) {
                //cancel the event
                ice.setCancelled(true);
                //close the inventory
                ice.getWhoClicked().closeInventory();
                //send the player a message saying they can't do it
                ((CommandSender) ice.getWhoClicked()).sendMessage(ChatColor.RED + "You don't have permission to use that ingredient!");
            }
        }
    }

    /**
     * Runs on a player eating
     * @param pee PlayerItemConsumeEvent
     */
    @EventHandler
    public void onPlayerEatEvent(PlayerItemConsumeEvent pee) {
        //if we're enabled and absorbtion is disabled
        if (isEnabled() && getConfigManager().getConfig("main").getBoolean(getBaseConfig() + "disableAbsorb")) {
            //if they ate a golden apple
            ItemStack is = pee.getItem();
            if (is.getType() == Material.GOLDEN_APPLE) {
                //remove the absorption effect for the player on the next tick
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(),new RemoveAbsoptionRunnable(pee.getPlayer().getUniqueId()));
            }
        }
    }

    /**
     * Triggered on an item moved in an inventory
     * @param imie InventoryMoveItemEvent
     */
    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent imie) {
        //if we're enabled
        if (isEnabled()) {
            FileConfiguration config = getConfigManager().getConfig("main");
            //if the item is being moved into a brewing stand
            if (imie.getDestination().getType() == InventoryType.BREWING) {
                //cancel sulpher if no permission
                if (imie.getItem().getType() == Material.SULPHUR && config.getBoolean(getBaseConfig()+"disableSplash")) {
                    imie.setCancelled(true);
                }
                //cancel glowstone if no permission
                if (imie.getItem().getType() == Material.GLOWSTONE_DUST && config.getBoolean(getBaseConfig()+"disableGlowstone")) {
                    imie.setCancelled(true);
                }
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "PotionNerfs";
    }

    @Override
    public String getDescription() {
        return "Applies nerfs to potions";
    }

    /**
     * When run removes absorption effect from the player name supplied
     */
    private static class RemoveAbsoptionRunnable implements Runnable{

        private final UUID m_playerID;

        /**
         * Removes absorption from a player when ran
         * @param playerID the player to run for
         */
        RemoveAbsoptionRunnable(UUID playerID){
            m_playerID = playerID;
        }

        @Override
        public void run() {
            Player p = Bukkit.getPlayer(m_playerID);
            if(p != null){
                p.removePotionEffect(PotionEffectType.ABSORPTION);
            }
        }
    }
}
