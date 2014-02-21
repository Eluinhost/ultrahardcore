package uk.co.eluinhost.ultrahardcore.features.core.entity;

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

    public ItemDrop(String groupName) {
        m_groupName = groupName;
    }

    public int getMinAmount() {
        return m_minAmount;
    }

    public void setMinAmount(int minAmount) {
        m_minAmount = minAmount;
    }

    public int getMaxAmount() {
        return m_maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        m_maxAmount = maxAmount;
    }

    public int getChance() {
        return m_dropChance;
    }

    public void setChance(int chance) {
        m_dropChance = chance;
    }

    public Material getItem() {
        return m_material;
    }

    public void setItem(Material item) {
        m_material = item;
    }

    public int getMeta() {
        return m_meta;
    }

    public void setMeta(int meta) {
        m_meta = meta;
    }

    public String getGroupName() {
        return m_groupName;
    }

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
