/*
 * ItemDrop.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.pluginfeatures.deathdrops;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ItemDrop {
    private int m_minAmount;
    private int m_maxAmount;
    private int m_dropChance;
    private Material m_material;
    private int m_meta;

    private static final Random RANDOM = new Random();

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
