package uk.co.eluinhost.UltraHardcore.features.core;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffectType;

import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;


/**
 * RecipeHandler
 * 
 * Handles the alternative recipies for the regen items
 * @author ghowden
 *
 */
public class RecipeFeature extends UHCFeature{

	public RecipeFeature(boolean enabled){
		super(enabled);
		setFeatureID("HardRecipes");
		setDescription("Handles changed recipes");
	}

	@EventHandler
	public void onPrepareCraftItemEvent(PrepareItemCraftEvent e){
		if(isEnabled()){
			Recipe r = e.getRecipe();
			if(r.getResult().getType().equals(Material.GOLDEN_APPLE)){
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
				if(recipeContainsMaterial(r,Material.GOLD_BLOCK)){
					if(!e.getView().getPlayer().hasPermission(PermissionNodes.ALLOW_NOTCH_APPLE)){
						e.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}
			}
			else if(r.getResult().getType().equals(Material.SPECKLED_MELON)){
				if(recipeContainsMaterial(r,Material.GOLD_NUGGET)){
					if(e.getView().getPlayer().hasPermission(PermissionNodes.DISALLOW_OLD_GMELON)){
						e.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}else if(recipeContainsMaterial(r,Material.GOLD_BLOCK)){
					if(!e.getView().getPlayer().hasPermission(PermissionNodes.ALLOW_NEW_GMELON)){
						e.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}
			}else if(r.getResult().getType().equals(Material.GOLDEN_CARROT)){
				if(recipeContainsMaterial(r,Material.GOLD_NUGGET)){
					if(e.getView().getPlayer().hasPermission(PermissionNodes.DISALLOW_OLD_GCARROT)){
						e.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}else if(recipeContainsMaterial(r,Material.GOLD_INGOT)){
					if(!e.getView().getPlayer().hasPermission(PermissionNodes.ALLOW_NEW_GCARROT)){
						e.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}
			}
		}
	}

	private boolean recipeContainsMaterial(Recipe r,Material mat){
		Collection<ItemStack> ingredients = null;
		if(r instanceof ShapedRecipe){
			ingredients = ((ShapedRecipe) r).getIngredientMap().values();
		}
		if(r instanceof ShapelessRecipe){
			ingredients = ((ShapelessRecipe)r).getIngredientList();
		}
		if(ingredients == null){
			return false;
		}
		for(ItemStack i : ingredients){
			if(i.getType().equals(mat)){
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
		ShapedRecipe newGoldenCarrot = new ShapedRecipe(new ItemStack(Material.GOLDEN_CARROT,1));
		//8 gold ingots surrounding an apple
		newGoldenCarrot.shape("AAA","ABA","AAA");
		newGoldenCarrot.setIngredient('A', Material.GOLD_INGOT);
		newGoldenCarrot.setIngredient('B', Material.CARROT_ITEM);
		Bukkit.addRecipe(newGoldenCarrot);
		
		//Make the recipe for glistering melons minus a set shape (glistering melon is speckled melon in code
		ShapelessRecipe newGlisteringMelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON,1));
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
		while(i.hasNext()){
			Recipe r = i.next();
			if(r.getResult().getType().equals(Material.SPECKLED_MELON)){
				if(recipeContainsMaterial(r,Material.GOLD_BLOCK)){
					i.remove();
				}
			}/*else if(r.getResult().getType().equals(Material.GOLDEN_APPLE)){
				if(recipeContainsMaterial(r,Material.GOLD_INGOT)){
					i.remove();
				}
			}*/else if(r.getResult().getType().equals(Material.GOLDEN_CARROT)){
				if(recipeContainsMaterial(r,Material.GOLD_INGOT)){
					i.remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent imie){
		if(isEnabled() && ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.RECIPE_CHANGES_SPLASH)){
			if(imie.getDestination().getType().equals(InventoryType.BREWING)){
				if(imie.getItem().getType().equals(Material.SULPHUR)){
					imie.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent ice){
		if(isEnabled() && ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.RECIPE_CHANGES_SPLASH)){
			if(ice.getInventory().getType().equals(InventoryType.BREWING)){
				if(!ice.getWhoClicked().hasPermission(PermissionNodes.DENY_SPLASH)){
					return;
				}
				InventoryView iv = ice.getView();
				boolean cancel = false;
				if(ice.isShiftClick()){
					if(ice.getCurrentItem().getType().equals(Material.SULPHUR)){
						cancel = true;
					}
				}else if(ice.getSlotType().equals(SlotType.FUEL)){
					if(iv.getCursor().getType().equals(Material.SULPHUR)){
						cancel = true;
					}
				}
				if(cancel){
					ice.setCancelled(true);
					ice.getWhoClicked().closeInventory();
					((Player)ice.getWhoClicked()).sendMessage(ChatColor.RED+"You don't have permission to brew splash potions!");
				}
			}
		}
	}
	@EventHandler
	public void onPlayerEatEvent(PlayerItemConsumeEvent pee){
		ItemStack is = pee.getItem();
		if(is.getType().equals(Material.GOLDEN_APPLE)){
			final String playerName = pee.getPlayer().getName();
			Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(),
					new Runnable(){
						@Override
						public void run() {
							Player p = Bukkit.getPlayerExact(playerName);
							PotionEffectType abs = PotionEffectType.getById(22);
							p.removePotionEffect(abs);
						}
					}
			);
		}
	}
}
