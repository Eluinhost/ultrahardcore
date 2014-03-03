package uk.co.eluinhost.ultrahardcore.features.deathdrops;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ItemDrop {
    private int m_minAmount;
    private int m_maxAmount;
    private int m_dropChance;
    private Material m_material;
    private int m_meta;
    private final String m_groupName;

    private static final Random RANDOM = new Random();

    /**
     * A new item drop
     * @param groupName group to be a part of
     */
    public ItemDrop(String groupName) {
        m_groupName = groupName;
    }

    /**
     * @return minimum amount to drop
     */
    public int getMinAmount() {
        return m_minAmount;
    }

    /**
     * @param minAmount the minimum amount to drop
     */
    public void setMinAmount(int minAmount) {
        m_minAmount = minAmount;
    }

    /**
     * @return the maximum amount to drop
     */
    public int getMaxAmount() {
        return m_maxAmount;
    }

    /**
     * @param maxAmount the maximum amount to drop
     */
    public void setMaxAmount(int maxAmount) {
        m_maxAmount = maxAmount;
    }

    /**
     * @return the chance 0-100 to drop
     */
    public int getChance() {
        return m_dropChance;
    }

    /**
     * @param chance the chance 0-100 to drop
     */
    public void setChance(int chance) {
        m_dropChance = chance;
    }

    /**
     * @return the item material
     */
    public Material getItem() {
        return m_material;
    }

    /**
     * @param item the item material
     */
    public void setItem(Material item) {
        m_material = item;
    }

    /**
     * @return the item metadata
     */
    public int getMeta() {
        return m_meta;
    }

    /**
     * @param  meta the item metadata
     */
    public void setMeta(int meta) {
        m_meta = meta;
    }

    /**
     * @return the name of the group it belongs to
     */
    public String getGroupName() {
        return m_groupName;
    }

    /**
     * This item drop as an item stack, amount between min-max
     * @return item stack
     */
    public ItemStack getItemStack() {
        int range = Math.max(0, getMaxAmount() - getMinAmount());
        int randomInt = RANDOM.nextInt(range + 1);
        int amount = getMinAmount() + randomInt;
        if (amount > 0) {
            return new ItemStack(getItem(), amount, (short) getMeta());
        }
        return null;
    }
}
