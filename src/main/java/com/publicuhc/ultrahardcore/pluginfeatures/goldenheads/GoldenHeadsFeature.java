/*
 * GoldenHeadsFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.goldenheads;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import com.publicuhc.ultrahardcore.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class GoldenHeadsFeature extends UHCFeature {

    public static final int POTION_TICK_MULTIPLIER = 25;
    public static final String HEAD_NAME = ChatColor.GOLD+"Golden Head";

    private ShapedRecipe m_headRecipe;

    /**
     * Adds a recipe to make golden heads
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private GoldenHeadsFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * When the player eats
     * @param pee the eat event
     */
    @EventHandler
    public void onPlayerEatEvent(PlayerItemConsumeEvent pee) {
        //if they ate a golden apple
        ItemStack is = pee.getItem();
        if (is.getType() == Material.GOLDEN_APPLE) {
            //if it was a golden head
            ItemMeta im = is.getItemMeta();
            if (im.hasDisplayName() && im.getDisplayName().equals(HEAD_NAME)) {
                //add tge new potion effect
                pee.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100 + getConfigManager().getConfig("main").getInt(getBaseConfig()+"amountExtra") * POTION_TICK_MULTIPLIER, 1));
            }
        }
    }

    /**
     * When trying to craft
     * @param pce the item craft event
     */
    @EventHandler
    public void onPreCraftEvent(PrepareItemCraftEvent pce) {
        //if it's the golden head recipe
        if (RecipeUtil.areSimilar(pce.getRecipe(), m_headRecipe)) {

            //get the result's metadata
            ItemStack res = pce.getInventory().getResult();
            ItemMeta meta = res.getItemMeta();

            //get the name of the used head
            String name = "INVALID PLAYER";
            for (ItemStack itemStack : pce.getInventory().getContents()) {
                if (itemStack.getType() == Material.SKULL_ITEM) {
                    SkullMeta sm = (SkullMeta) itemStack.getItemMeta();
                    name = sm.getOwner();
                }
            }

            //set the lore on the output golden head
            List<String> lore = meta.getLore();
            lore.add(ChatColor.AQUA + "Made from the head of: " + name);
            meta.setLore(lore);
            res.setItemMeta(meta);

            //set the resulting head
            pce.getInventory().setResult(res);
        }
    }

    @Override
    protected void enableCallback() {
        //Make a recipe that will return a golden apple when the right shape is made
        ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(HEAD_NAME);

        //add it's lore
        List<String> lore = new ArrayList<String>();
        lore.add("Some say consuming the head of a");
        lore.add("fallen foe strengthens the blood");
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        //make the recipe
        ShapedRecipe goldenHead = new ShapedRecipe(itemStack);
        //8 gold ingots surrounding an apple
        goldenHead.shape("AAA", "ABA", "AAA");
        //noinspection deprecation
        goldenHead.setIngredient('A', Material.GOLD_INGOT)
                .setIngredient('B', Material.SKULL_ITEM, 3); //TODO deprecated but no alternative?

        m_headRecipe = goldenHead;

        //add recipe to bukkit
        Bukkit.addRecipe(goldenHead);
    }

    @Override
    protected void disableCallback() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe r = recipeIterator.next();
            ItemStack is = r.getResult();
            //if it's a golden apple named like ours remove it
            if (is.getType() == Material.GOLDEN_APPLE) {
                ItemMeta im = is.getItemMeta();
                if (im.hasDisplayName() && im.getDisplayName().equals(HEAD_NAME)) {
                    recipeIterator.remove();
                }
            }
        }
    }

    public void setAmountExtra(int amountExtra) {
        getConfigManager().getConfig("main").set(getBaseConfig() + "amountExtra", amountExtra);
    }

    @Override
    public String getFeatureID() {
        return "GoldenHeads";
    }

    @Override
    public String getDescription() {
        return "New and improved golden apples!";
    }

    @Override
    public List<String> getStatus() {
        List<String> status = new ArrayList<String>();
        status.add(ChatColor.GRAY + "--- Extra hearts healed: " + getConfigManager().getConfig("main").getInt(getBaseConfig()+"amountExtra"));
        return status;
    }
}
