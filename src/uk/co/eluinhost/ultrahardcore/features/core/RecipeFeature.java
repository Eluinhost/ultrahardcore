package uk.co.eluinhost.ultrahardcore.features.core;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import org.bukkit.permissions.Permissible;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * RecipeHandler
 * Handles the alternative recipies for the regen items
 *
 * @author ghowden
 */
public class RecipeFeature extends UHCFeature {

    public RecipeFeature(boolean enabled) {
        super("HardRecipes", enabled);
        setDescription("Handles changed recipes");
    }

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
     * TODO abstract this into a config style instead of hardcode and loop
     */
    private static boolean isRecipeAllowedForPermissible(Permissible permissible, Recipe recipe){
        Material result = recipe.getResult().getType();

        if(result == Material.GOLDEN_APPLE && hasRecipeGotMaterial(recipe, Material.GOLD_BLOCK) && !permissible.hasPermission(PermissionNodes.ALLOW_NOTCH_APPLE)){
            return false;
        }
        if(result == Material.SPECKLED_MELON && hasRecipeGotMaterial(recipe, Material.GOLD_NUGGET) && permissible.hasPermission(PermissionNodes.DISALLOW_OLD_GMELON)){
            return false;
        }
        if(result == Material.SPECKLED_MELON && hasRecipeGotMaterial(recipe, Material.GOLD_BLOCK) && permissible.hasPermission(PermissionNodes.ALLOW_NEW_GMELON)) {
            return false;
        }
        if(result == Material.GOLDEN_CARROT && hasRecipeGotMaterial(recipe, Material.GOLD_NUGGET) && permissible.hasPermission(PermissionNodes.DISALLOW_OLD_GCARROT)) {
            return false;
        }
        if(result == Material.GOLDEN_CARROT && hasRecipeGotMaterial(recipe, Material.GOLD_INGOT) && permissible.hasPermission(PermissionNodes.ALLOW_NEW_GCARROT)) {
            return false;
        }
        //passed all checks
        return true;
    }

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
}
