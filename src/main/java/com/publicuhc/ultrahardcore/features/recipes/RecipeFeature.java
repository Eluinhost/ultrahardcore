package com.publicuhc.ultrahardcore.features.recipes;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.features.UHCFeature;

import java.util.Collection;
import java.util.Iterator;


/**
 * RecipeHandler
 * Handles the alternative recipies for the regen items
 *
 * @author ghowden
 */
@Singleton
public class RecipeFeature extends UHCFeature {

    public static final String RECIPE_BASE = BASE_PERMISSION + "recipies.";
    public static final String ALLOW_NEW_GMELON = RECIPE_BASE + "allowNewGMelon";
    public static final String DISALLOW_OLD_GMELON = RECIPE_BASE + "disableGMelon";
    public static final String ALLOW_NOTCH_APPLE = RECIPE_BASE + "allowNotchApple";
    public static final String ALLOW_NEW_GCARROT = RECIPE_BASE + "allowNewGCarrot";
    public static final String DISALLOW_OLD_GCARROT = RECIPE_BASE + "disableGCarrot";

    /**
     * Harder recipes when enabled, normal when disabled
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    private RecipeFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
    }

    /**
     * Whenever an item is about to pop up on the crafting table
     * @param e the prepareitemcraftevent
     */
    @EventHandler
    public void onPrepareCraftItemEvent(PrepareItemCraftEvent e) {
        //if we are enabled
        if (isEnabled()) {
            //check if no permission to create
            if(!isRecipeAllowedForPermissible(e.getView().getPlayer(),e.getRecipe())){
                //set to air if not allowed
                e.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    /**
     * Is the recipe allowed?
     * @param permissible Permissible the permissible to check against
     * @param recipe the recipe to check
     * @return boolean true if allowed, false if not
     */
    private static boolean isRecipeAllowedForPermissible(Permissible permissible, Recipe recipe){
        Material result = recipe.getResult().getType();

        if(result == Material.GOLDEN_APPLE && hasRecipeGotMaterial(recipe, Material.GOLD_BLOCK) && !permissible.hasPermission(ALLOW_NOTCH_APPLE)){
            return false;
        }
        if(result == Material.SPECKLED_MELON && hasRecipeGotMaterial(recipe, Material.GOLD_NUGGET) && permissible.hasPermission(DISALLOW_OLD_GMELON)){
            return false;
        }
        if(result == Material.SPECKLED_MELON && hasRecipeGotMaterial(recipe, Material.GOLD_BLOCK) && permissible.hasPermission(ALLOW_NEW_GMELON)) {
            return false;
        }
        if(result == Material.GOLDEN_CARROT && hasRecipeGotMaterial(recipe, Material.GOLD_NUGGET) && permissible.hasPermission(DISALLOW_OLD_GCARROT)) {
            return false;
        }
        if(result == Material.GOLDEN_CARROT && hasRecipeGotMaterial(recipe, Material.GOLD_INGOT) && permissible.hasPermission(ALLOW_NEW_GCARROT)) {
            return false;
        }
        //passed all checks
        return true;
    }

    /**
     * Check if the recipe has the given material in it
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

    /**
     * Add our recipes
     */
    @Override
    protected void enableCallback() {
        //Make a recipe that will return a golden carrot when the right shape is made
        ShapedRecipe newGoldenCarrot = new ShapedRecipe(new ItemStack(Material.GOLDEN_CARROT, 1));
        //8 gold ingots surrounding an apple
        newGoldenCarrot.shape("AAA", "ABA", "AAA");
        newGoldenCarrot.setIngredient('A', Material.GOLD_INGOT);
        newGoldenCarrot.setIngredient('B', Material.CARROT_ITEM);
        Bukkit.addRecipe(newGoldenCarrot);

        //Make the recipe for glistering melons minus a set shape (glistering melon is speckled melon in code
        ShapelessRecipe newGlisteringMelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
        //1 gold ingot with a melon
        newGlisteringMelon.addIngredient(Material.GOLD_BLOCK);
        newGlisteringMelon.addIngredient(Material.MELON);
        Bukkit.addRecipe(newGlisteringMelon);
    }

    /**
     * Remove the recipes we added
     */
    @Override
    protected void disableCallback() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            boolean removeRecipe = false;
            if (recipe.getResult().getType() == Material.SPECKLED_MELON && hasRecipeGotMaterial(recipe, Material.GOLD_BLOCK)) {
                removeRecipe = true;
            }
            if (recipe.getResult().getType() == Material.GOLDEN_CARROT && hasRecipeGotMaterial(recipe, Material.GOLD_INGOT)) {
                removeRecipe = true;
            }
            if(removeRecipe){
                recipeIterator.remove();
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "HardRecipes";
    }

    @Override
    public String getDescription() {
        return "Handles changed recipes";
    }
}
