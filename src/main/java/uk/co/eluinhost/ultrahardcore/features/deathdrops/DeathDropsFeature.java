package uk.co.eluinhost.ultrahardcore.features.deathdrops;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


/**
 * DeathDrops
 * Handles extra drops on death
 *
 * @author ghowden
 */
@Singleton
public class DeathDropsFeature extends UHCFeature {

    private final Collection<ItemDrop> m_drops = new ArrayList<ItemDrop>();
    private static final Random RANDOM = new Random();

    public static final String ITEMS_NODE = "items";

    /**
     * Add drops to a player when they die
     * TODO simplify
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    public DeathDropsFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
        ConfigurationSection items = configManager.getConfig().getConfigurationSection(getBaseConfig()+ITEMS_NODE);
        for (String item : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(item);

            String itemIDString = itemSection.getString("id");
            if (itemIDString == null) {
                plugin.getLogger().severe("Death drop item section \"" + item + "\" does not contain the subnode 'id'");
                continue;
            }

            String amountString = itemSection.getString("amount");
            if (amountString == null) {
                plugin.getLogger().severe("Death drop item section \"" + item + "\" does not contain the subnode 'amount'");
                continue;
            }
            int minAmount = -1;
            if (amountString.contains("-")) {
                String[] amountParts = amountString.split("-");
                if (amountParts.length != 2) {
                    plugin.getLogger().severe("Death drop item section \"" + item + "\" does not contain a valid amount. Syntax 'amount' or 'amount_min-amount_max'");
                    continue;
                }
                try {
                    minAmount = Integer.parseInt(amountParts[0]);
                    amountString = amountParts[1];
                } catch (NumberFormatException ignored) {
                    plugin.getLogger().severe("Death drop item section \"" + item + "\" has an invalid minimum amount " + amountParts[0]);
                    continue;
                }
            }
            int maxAmount;
            try {
                maxAmount = Integer.parseInt(amountString);
                if (minAmount == -1) {
                    minAmount = maxAmount;
                }
            } catch (NumberFormatException ignored) {
                plugin.getLogger().severe("Death drop item section \"" + item + "\" contains invalid amount " + amountString);
                continue;
            }

            int chance = itemSection.getInt("chance",-1);
            if (chance < 0) {
                plugin.getLogger().severe("Death drop item section \"" + item + "\" does not contain a valid chance value, assuming 100%");
                chance = 100;
            }

            int metaID = 0;
            if (itemIDString.contains(":")) {
                String[] itemParts = itemIDString.split(":");
                if (itemParts.length != 2) {
                    plugin.getLogger().severe("Death drop item section \"" + item + "\" does not contain a valid item id. Syntax 'item_id' or 'item_id:meta'");
                    continue;
                }
                try {
                    metaID = Integer.parseInt(itemParts[1]);
                    itemIDString = itemParts[0];
                } catch (NumberFormatException ignored) {
                    plugin.getLogger().severe("Death drop item section \"" + item + "\" has an invalid metadata value " + itemParts[1]);
                    continue;
                }
            }

            int itemID;
            try {
                itemID = Integer.parseInt(itemIDString);
            } catch (NumberFormatException ignored) {
                plugin.getLogger().severe("Death drop item section \"" + item + "\" contains invalid id " + itemIDString);
                continue;
            }

            //noinspection deprecation
            Material mat = Material.getMaterial(itemID);      //TODO change feature to use names in config file instead of IDs and use getByName
            if (mat == null) {
                plugin.getLogger().severe("Death drop item section \"" + item + "\" contains invalid item id " + itemID + ": item for id not found");
                continue;
            }

            ItemDrop id = new ItemDrop();
            id.setItem(mat);
            id.setMeta(metaID);
            id.setChance(chance);
            id.setMaxAmount(maxAmount);
            id.setMinAmount(minAmount);
            m_drops.add(id);
        }
    }

    /**
     * Whenever a player dies
     * @param pde the death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (isEnabled()) {
            for (ItemDrop id : m_drops) {
                int randomInt = RANDOM.nextInt(100);
                if (randomInt < id.getChance()) {
                    ItemStack is = id.getItemStack();
                    if (is != null) {
                        pde.getDrops().add(is);
                    }
                }
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "DeathDrops";
    }

    @Override
    public String getDescription() {
        return "Adds extra loot to players on death";
    }
}
