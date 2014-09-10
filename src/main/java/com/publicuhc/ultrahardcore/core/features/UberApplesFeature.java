/*
 * UberApplesFeature.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.ultrahardcore.core.features;

import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.api.UHCFeature;
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
 * <p/>
 * Enabled: Disables uber apple crafting
 * Disabled: Nothing
 */
@Singleton
public class UberApplesFeature extends UHCFeature
{

    public static final String ALLOW_NOTCH_APPLE = "UHC.recipies.allowNotchApple";

    /**
     * Is the recipe allowed?
     *
     * @param permissible Permissible the permissible to check against
     * @param recipe      the recipe to check
     * @return boolean true if allowed, false if not
     */
    private static boolean isRecipeAllowedForPermissible(Permissible permissible, Recipe recipe)
    {
        Material result = recipe.getResult().getType();
        return result != Material.GOLDEN_APPLE || !hasRecipeGotMaterial(recipe, Material.GOLD_BLOCK) || permissible.hasPermission(ALLOW_NOTCH_APPLE);
    }

    /**
     * Check if the recipe has the given material in it
     *
     * @param r   the recipe to check
     * @param mat the material to look for
     * @return true if found, false if not
     */
    private static boolean hasRecipeGotMaterial(Recipe r, Material mat)
    {
        Collection<ItemStack> ingredients = null;
        //noinspection ChainOfInstanceofChecks
        if(r instanceof ShapedRecipe) {
            ingredients = ((ShapedRecipe) r).getIngredientMap().values();
        } else if(r instanceof ShapelessRecipe) {
            ingredients = ((ShapelessRecipe) r).getIngredientList();
        }
        return null != ingredients && isMaterialInList(ingredients, mat);
    }

    /**
     * Checks if the material is in the lsit
     *
     * @param itemStackList the list to check
     * @param mat           the material to look for
     * @return true if found, false if not
     */
    private static boolean isMaterialInList(Iterable<ItemStack> itemStackList, Material mat)
    {
        for(ItemStack itemStack : itemStackList) {
            if(itemStack.getType() == mat) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPrepareCraftItemEvent(PrepareItemCraftEvent e)
    {
        //if we are enabled
        if(isEnabled() && !isRecipeAllowedForPermissible(e.getView().getPlayer(), e.getRecipe())) {
            //set to air if not allowed
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @Override
    public String getFeatureID()
    {
        return "DisableUberApples";
    }

    @Override
    public String getDescription()
    {
        return "Disables crafting uber apples when enabled";
    }
}
