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

    public static final int POTION_TICK_MULTIPLIER = 25;
    public static final String HEAD_NAME = ChatColor.GOLD+"Golden Head";

    private ShapedRecipe m_headRecipe = null;

    public GoldenHeads(boolean enabled) {
        super("GoldenHeads", enabled);
        setDescription("New and improved golden apples!");
    }

    @EventHandler
    public static void onPlayerEatEvent(PlayerItemConsumeEvent pee) {
        //if they ate a golden apple
        ItemStack is = pee.getItem();
        if (is.getType() == Material.GOLDEN_APPLE) {
            //if it was a golden head
            ItemMeta im = is.getItemMeta();
            if (im.hasDisplayName() && im.getDisplayName().equals(HEAD_NAME)) {
                //get the factor of healing it's supposed to do
                int factor = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.GOLDEN_HEADS_HEAL_FACTOR);
                //add tge new potion effect
                pee.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100 + factor * POTION_TICK_MULTIPLIER, 1));
            }
        }
    }

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
    public void enableFeature() {
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
    public void disableFeature() {
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
}
