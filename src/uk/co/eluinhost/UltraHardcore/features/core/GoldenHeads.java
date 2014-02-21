package uk.co.eluinhost.UltraHardcore.features.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;
import uk.co.eluinhost.UltraHardcore.util.RecipeUtil;

public class GoldenHeads extends UHCFeature {

    private ShapedRecipe head_recipe = null;

    public GoldenHeads(boolean enabled) {
        super("GoldenHeads", enabled);
        setDescription("New and improved golden apples!");
    }

    @EventHandler
    public void onPlayerEatEvent(PlayerItemConsumeEvent pee) {
        ItemStack is = pee.getItem();
        if (is.getType().equals(Material.GOLDEN_APPLE)) {
            ItemMeta im = is.getItemMeta();
            if (im.hasDisplayName() && im.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
                int factor = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.GOLDEN_HEADS_HEAL_FACTOR);

                pee.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100 + (factor * 25), 1));
            }
        }
    }

    @EventHandler
    public void onPreCraftEvent(PrepareItemCraftEvent pce) {
        if (RecipeUtil.areSimilar(pce.getRecipe(), head_recipe)) {
            ItemStack res = pce.getInventory().getResult();
            ItemMeta meta = res.getItemMeta();

            String name = "N/A";

            for (ItemStack i : pce.getInventory().getContents()) {
                if (i.getType().equals(Material.SKULL_ITEM)) {
                    SkullMeta sm = (SkullMeta) i.getItemMeta();
                    name = sm.getOwner();
                }
            }

            List<String> lore = meta.getLore();
            lore.add(ChatColor.AQUA + "Made from the head of: " + name);
            meta.setLore(lore);
            res.setItemMeta(meta);
            pce.getInventory().setResult(res);
        }
    }

    @Override
    public void enableFeature() {
        //Make a recipe that will return a golden apple when the right shape is made
        ItemStack i = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Golden Head");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("Some say consuming the head of a");
        lore.add("fallen foe strengthens the blood");
        meta.setLore(lore);
        i.setItemMeta(meta);
        ShapedRecipe golden_head = new ShapedRecipe(i);
        //8 gold ingots surrounding an apple
        golden_head.shape("AAA", "ABA", "AAA");
        golden_head.setIngredient('A', Material.GOLD_INGOT)
                .setIngredient('B', Material.SKULL_ITEM, 3); //TODO deprecated but no alternative?

        head_recipe = golden_head;
        Bukkit.addRecipe(golden_head);
    }

    @Override
    public void disableFeature() {
        Iterator<Recipe> i = Bukkit.recipeIterator();
        while (i.hasNext()) {
            Recipe r = i.next();
            ItemStack is = r.getResult();
            if (is.getType().equals(Material.GOLDEN_APPLE)) {
                ItemMeta im = is.getItemMeta();
                if (im.hasDisplayName() && im.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
                    i.remove();
                }
            }
        }
    }
}
