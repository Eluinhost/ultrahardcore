package uk.co.eluinhost.ultrahardcore.features.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;


/**
 * DeathDrops
 * <p/>
 * Handles extra drops on death
 *
 * @author ghowden
 */
public class DeathDrops extends UHCFeature {

    private List<ItemDrop> drops = new ArrayList<ItemDrop>();
    private Random r = new Random();

    public class ItemDrop {
        private int minAmount = 0;
        private int maxAmount = 0;
        private int chance = 0;
        private Material item;
        private int meta;
        private String groupName;

        public ItemDrop(String name) {
            groupName = name;
        }

        public int getMinAmount() {
            return minAmount;
        }

        public void setMinAmount(int minAmount) {
            this.minAmount = minAmount;
        }

        public int getMaxAmount() {
            return maxAmount;
        }

        public void setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
        }

        public int getChance() {
            return chance;
        }

        public void setChance(int chance) {
            this.chance = chance;
        }

        public Material getItem() {
            return item;
        }

        public void setItem(Material item) {
            this.item = item;
        }

        public int getMeta() {
            return meta;
        }

        public void setMeta(int meta) {
            this.meta = meta;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setItemName(String groupName) {
            this.groupName = groupName;
        }

        public ItemStack getItemStack() {
            int range = Math.max(0, getMaxAmount() - getMinAmount());
            int d = r.nextInt(range + 1);
            int amount = getMinAmount() + d;
            if (amount > 0) {
                return new ItemStack(getItem(), amount, (short) getMeta());
            }
            return null;
        }
    }

    public DeathDrops(boolean enabled) {
        super("DeathDrops", enabled);
        setDescription("Adds extra loot to players on death");
        ConfigurationSection items = ConfigHandler.getConfig(ConfigHandler.MAIN).getConfigurationSection(ConfigNodes.DEATH_DROPS_ITEMS);
        for (String item : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(item);
            String itemID_s = itemSection.getString("id");
            String amount_s = itemSection.getString("amount");
            String chance_s = itemSection.getString("chance");
            int itemID;
            int metaID = 0;
            int amount_min = -1;
            int amount_max;
            int chance;
            if (itemID_s == null) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain the subnode 'id'");
                continue;
            }
            if (amount_s == null) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain the subnode 'amount'");
                continue;
            }
            if (chance_s == null) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain the subnode 'chance', assuming 100%");
                chance_s = "100";
            }
            try {
                chance = Integer.parseInt(chance_s);
            } catch (Exception ex) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" contains invalid chance " + chance_s);
                continue;
            }
            if (itemID_s.contains(":")) {
                String[] itemParts = itemID_s.split(":");
                if (itemParts.length != 2) {
                    UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain a valid item id. Syntax 'item_id' or 'item_id:meta'");
                    continue;
                }
                try {
                    metaID = Integer.parseInt(itemParts[1]);
                    itemID_s = itemParts[0];
                } catch (Exception ex) {
                    UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" has an invalid metadata value " + itemParts[1]);
                    continue;
                }
            }
            try {
                itemID = Integer.parseInt(itemID_s);
            } catch (Exception ex) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" contains invalid id " + itemID_s);
                continue;
            }
            if (amount_s.contains("-")) {
                String[] amountParts = amount_s.split("-");
                if (amountParts.length != 2) {
                    UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" does not contain a valid amount. Syntax 'amount' or 'amount_min-amount_max'");
                    continue;
                }
                try {
                    amount_min = Integer.parseInt(amountParts[0]);
                    amount_s = amountParts[1];
                } catch (Exception ex) {
                    UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" has an invalid minimum amount " + amountParts[0]);
                    continue;
                }
            }
            try {
                amount_max = Integer.parseInt(amount_s);
                if (amount_min == -1) {
                    amount_min = amount_max;
                }
            } catch (Exception ex) {
                UltraHardcore.getInstance().getLogger().severe("Death drop item section \"" + item + "\" contains invalid amount " + amount_s);
                continue;
            }
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
            id.setMaxAmount(amount_max);
            id.setMinAmount(amount_min);
            drops.add(id);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (isEnabled()) {
            for (ItemDrop id : drops) {
                int randomInt = r.nextInt(100);
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
        for (ItemDrop id : drops) {
            if (id.getGroupName().equalsIgnoreCase(name)) {
                ids.add(id);
            }
        }
        return ids;
    }

    public List<String> getItemDropGroups() {
        ArrayList<String> list = new ArrayList<String>();
        for (ItemDrop id : drops) {
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

    @Override
    public void enableFeature() {
    }

    @Override
    public void disableFeature() {
    }
}
