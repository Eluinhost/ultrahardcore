package uk.co.eluinhost.ultrahardcore.features.deathdrops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.services.ConfigManager;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;
import uk.co.eluinhost.ultrahardcore.features.deathdrops.ItemDrop;


/**
 * DeathDrops
 * Handles extra drops on death
 *
 * @author ghowden
 */
public class DeathDropsFeature extends UHCFeature {

    private final Collection<ItemDrop> m_drops = new ArrayList<ItemDrop>();
    private static final Random RANDOM = new Random();

    //TODO simplify...
    public DeathDropsFeature() {
        super("DeathDrops", "Adds extra loot to players on death");
        ConfigurationSection items = ConfigManager.getConfig(ConfigManager.MAIN).getConfigurationSection(ConfigNodes.DEATH_DROPS_ITEMS);
        for (String item : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(item);

            String itemIDString = itemSection.getString("id");
            if (itemIDString == null) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain the subnode 'id'");
                continue;
            }

            String amountString = itemSection.getString("amount");
            if (amountString == null) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain the subnode 'amount'");
                continue;
            }
            int minAmount = -1;
            if (amountString.contains("-")) {
                String[] amountParts = amountString.split("-");
                if (amountParts.length != 2) {
                    UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain a valid amount. Syntax 'amount' or 'amount_min-amount_max'");
                    continue;
                }
                try {
                    minAmount = Integer.parseInt(amountParts[0]);
                    amountString = amountParts[1];
                } catch (NumberFormatException ignored) {
                    UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" has an invalid minimum amount " + amountParts[0]);
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
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" contains invalid amount " + amountString);
                continue;
            }

            int chance = itemSection.getInt("chance",-1);
            if (chance < 0) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain a valid chance value, assuming 100%");
                chance = 100;
            }

            int metaID = 0;
            if (itemIDString.contains(":")) {
                String[] itemParts = itemIDString.split(":");
                if (itemParts.length != 2) {
                    UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain a valid item id. Syntax 'item_id' or 'item_id:meta'");
                    continue;
                }
                try {
                    metaID = Integer.parseInt(itemParts[1]);
                    itemIDString = itemParts[0];
                } catch (NumberFormatException ignored) {
                    UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" has an invalid metadata value " + itemParts[1]);
                    continue;
                }
            }

            int itemID;
            try {
                itemID = Integer.parseInt(itemIDString);
            } catch (NumberFormatException ignored) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" contains invalid id " + itemIDString);
                continue;
            }

            //noinspection deprecation
            Material mat = Material.getMaterial(itemID);      //TODO change feature to use names in config file instead of IDs and use getByName
            if (mat == null) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" contains invalid item id " + itemID + ": item for id not found");
                continue;
            }

            String group = itemSection.getString("group");
            if (group == null) {
                group = "N/A";
            }

            ItemDrop id = new ItemDrop(group);
            id.setItem(mat);
            id.setMeta(metaID);
            id.setChance(chance);
            id.setMaxAmount(maxAmount);
            id.setMinAmount(minAmount);
            m_drops.add(id);
        }
    }

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

    public List<ItemDrop> getItemDropForGroup(String name) {
        List<ItemDrop> ids = new ArrayList<ItemDrop>();
        for (ItemDrop id : m_drops) {
            if (id.getGroupName().equalsIgnoreCase(name)) {
                ids.add(id);
            }
        }
        return ids;
    }

    public List<String> getItemDropGroups() {
        ArrayList<String> list = new ArrayList<String>();
        for (ItemDrop id : m_drops) {
            boolean found = false;
            for (String s : list) {
                if (s.equalsIgnoreCase(id.getGroupName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                list.add(id.getGroupName());
            }
        }
        return list;
    }
}
