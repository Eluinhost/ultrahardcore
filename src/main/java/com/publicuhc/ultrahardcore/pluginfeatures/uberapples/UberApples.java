/*
 * RecipeFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.uberapples;

import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.permissions.Permissible;

import java.util.Collection;


/**
 * UberApples
 *
 * Enabled: Disables uber apple crafting
 * Disabled: Nothing
 */
@Singleton
public class UberApples extends UHCFeature {

    public static final String ALLOW_NOTCH_APPLE = "UHC.recipies.allowNotchApple";

    @EventHandler
    public void onPrepareCraftItemEvent(PrepareItemCraftEvent e) {
        //if we are enabled
        if (isEnabled() && !isRecipeAllowedForPermissible(e.getView().getPlayer(),e.getRecipe())){
            //set to air if not allowed
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    /**
     * Is the recipe allowed?
     *
     * @param permissible Permissible the permissible to check against
     * @param recipe the recipe to check
     * @return boolean true if allowed, false if not
     */
    private static boolean isRecipeAllowedForPermissible(Permissible permissible, Recipe recipe){
        Material result = recipe.getResult().getType();
        return result != Material.GOLDEN_APPLE || !hasRecipeGotMaterial(recipe, Material.GOLD_BLOCK) || permissible.hasPermission(ALLOW_NOTCH_APPLE);
    }

    /**
     * Check if the recipe has the given material in it
     *
     * @param r the recipe to check
     * @param mat the material to look for
     * @return true if found, false if not
     */
    private static boolean hasRecipeGotMaterial(Recipe r, Material mat) {
        Collection<ItemStack> ingredients = null;
        //noinspection ChainOfInstanceofChecks
        if (r instanceof ShapedRecipe) {
            ingredients = ((ShapedRecipe) r).getIngredientMap().values();
        }else if (r instanceof ShapelessRecipe) {
            ingredients = ((ShapelessRecipe) r).getIngredientList();
        }
        return null != ingredients && isMaterialInList(ingredients, mat);
    }

    /**
     * Checks if the material is in the lsit
     * @param itemStackList the list to check
     * @param mat the material to look for
     * @return true if found, false if not
     */
    private static boolean isMaterialInList(Iterable<ItemStack> itemStackList, Material mat){
        for (ItemStack itemStack : itemStackList) {
            if (itemStack.getType() == mat) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFeatureID() {
        return "DisableUberApples";
    }

    @Override
    public String getDescription() {
        return "Disables crafting uber apples when enabled";
    }
}
