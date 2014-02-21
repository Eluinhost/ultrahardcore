package uk.co.eluinhost.UltraHardcore.features.core;

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

import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;


/**
 * RecipeHandler
 * <p/>
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
        if (isEnabled()) {
            Recipe r = e.getRecipe();
            if (r.getResult().getType().equals(Material.GOLDEN_APPLE)) {
                /*if(recipeContainsMaterial(r,Material.GOLD_NUGGET)){
					if(e.getView().getPlayer().hasPermission(PermissionNodes.DISALLOW_OLD_GAPPLE)){
						e.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}else if(recipeContainsMaterial(r,Material.GOLD_INGOT)){
					if(!e.getView().getPlayer().hasPermission(PermissionNodes.ALLOW_NEW_GAPPLE)){
						e.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}else 
				*/
                if (recipeContainsMaterial(r, Material.GOLD_BLOCK)) {
                    if (!e.getView().getPlayer().hasPermission(PermissionNodes.ALLOW_NOTCH_APPLE)) {
                        e.getInventory().setResult(new ItemStack(Material.AIR));
                    }
                }
            } else if (r.getResult().getType().equals(Material.SPECKLED_MELON)) {
                if (recipeContainsMaterial(r, Material.GOLD_NUGGET)) {
                    if (e.getView().getPlayer().hasPermission(PermissionNodes.DISALLOW_OLD_GMELON)) {
                        e.getInventory().setResult(new ItemStack(Material.AIR));
                    }
                } else if (recipeContainsMaterial(r, Material.GOLD_BLOCK)) {
                    if (!e.getView().getPlayer().hasPermission(PermissionNodes.ALLOW_NEW_GMELON)) {
                        e.getInventory().setResult(new ItemStack(Material.AIR));
                    }
                }
            } else if (r.getResult().getType().equals(Material.GOLDEN_CARROT)) {
                if (recipeContainsMaterial(r, Material.GOLD_NUGGET)) {
                    if (e.getView().getPlayer().hasPermission(PermissionNodes.DISALLOW_OLD_GCARROT)) {
                        e.getInventory().setResult(new ItemStack(Material.AIR));
                    }
                } else if (recipeContainsMaterial(r, Material.GOLD_INGOT)) {
                    if (!e.getView().getPlayer().hasPermission(PermissionNodes.ALLOW_NEW_GCARROT)) {
                        e.getInventory().setResult(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    private boolean recipeContainsMaterial(Recipe r, Material mat) {
        Collection<ItemStack> ingredients = null;
        if (r instanceof ShapedRecipe) {
            ingredients = ((ShapedRecipe) r).getIngredientMap().values();
        }
        if (r instanceof ShapelessRecipe) {
            ingredients = ((ShapelessRecipe) r).getIngredientList();
        }
        if (ingredients == null) {
            return false;
        }
        for (ItemStack i : ingredients) {
            if (i.getType().equals(mat)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add our recipes
     */
    @Override
    public void enableFeature() {
		/*
		//Make a recipe that will return a golden apple when the right shape is made
		ShapedRecipe newGoldenApple = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE,1));
		//8 gold ingots surrounding an apple
		newGoldenApple.shape("AAA","ABA","AAA");
		newGoldenApple.setIngredient('A', Material.GOLD_INGOT);
		newGoldenApple.setIngredient('B', Material.APPLE);
		Bukkit.addRecipe(newGoldenApple);
		*/
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
    public void disableFeature() {
        Iterator<Recipe> i = Bukkit.recipeIterator();
        while (i.hasNext()) {
            Recipe r = i.next();
            if (r.getResult().getType().equals(Material.SPECKLED_MELON)) {
                if (recipeContainsMaterial(r, Material.GOLD_BLOCK)) {
                    i.remove();
                }
            }/*else if(r.getResult().getType().equals(Material.GOLDEN_APPLE)){
				if(recipeContainsMaterial(r,Material.GOLD_INGOT)){
					i.remove();
				}
			}*/ else if (r.getResult().getType().equals(Material.GOLDEN_CARROT)) {
                if (recipeContainsMaterial(r, Material.GOLD_INGOT)) {
                    i.remove();
                }
            }
        }
    }
}
