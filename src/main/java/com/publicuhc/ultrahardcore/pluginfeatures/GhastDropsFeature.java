/*
 * GhastDropsFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * GhastDropsFeature
 *
 * Enabled: Replaces ghast tears with gold ingots on death of ghast
 * Disabled: Nothing
 */
@Singleton
public class GhastDropsFeature extends UHCFeature {

    private final FileConfiguration config;

    /**
     * Stops ghasts dropping tears to get rid of regen potions
     *
     * @param configManager the config manager
     */
    @Inject
    private GhastDropsFeature(Configurator configManager) {
        config = configManager.getConfig("main");
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent ede) {
        //if we're enabled and a ghast died
        if (isEnabled() && ede.getEntityType() == EntityType.GHAST) {
            //if ghasts can't drop tears in this world
            if (featureEnabledForWorld(ede.getEntity().getWorld().getName())) {

                //get the list of drops
                List<ItemStack> drops = ede.getDrops();

                //for all the items dropped
                Iterator<ItemStack> iterator = drops.iterator();
                Collection<ItemStack> toAdd = new ArrayList<ItemStack>();
                while (iterator.hasNext()) {
                    ItemStack is = iterator.next();
                    //if it was a ghast tear drop the same amount of gold ingots
                    if (is.getType() == Material.GHAST_TEAR) {
                        iterator.remove();
                        toAdd.add(new ItemStack(Material.GOLD_INGOT, is.getAmount()));
                    }
                }
                drops.addAll(toAdd);
            }
        }
    }

    /**
     * Is the feature enabled for the world given
     *
     * @param worldName the world
     * @return true if allowed, false otherwise
     */
    public boolean featureEnabledForWorld(String worldName) {
        //w&&f = enabled
        //w&&!f = disabled
        //!w&&f = disabled
        //!w&&!f = enabled
        // = AND
        boolean whitelist = config.getBoolean("GhastDrops.whitelist");
        boolean found = config.getStringList("GhastDrops.worlds").contains(worldName);
        return !(whitelist ^ found);
    }

    @Override
    public String getFeatureID() {
        return "GhastDrops";
    }

    @Override
    public String getDescription() {
        return "Ghasts drop golden ingots instead of tears";
    }
}
