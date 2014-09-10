/*
 * GhastDropsFeature.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.ultrahardcore.core.features;

import com.google.common.base.Optional;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.api.UHCFeature;
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
 * <p/>
 * Enabled: Replaces ghast tears with gold ingots on death of ghast
 * Disabled: Nothing
 */
@Singleton
public class GhastDropsFeature extends UHCFeature
{

    private final FileConfiguration config;

    /**
     * Stops ghasts dropping tears to get rid of regen potions
     *
     * @param configManager the config manager
     */
    @Inject
    private GhastDropsFeature(Configurator configManager)
    {
        Optional<FileConfiguration> mainConfig = configManager.getConfig("main");
        if(!mainConfig.isPresent()) {
            throw new IllegalStateException("Config file 'main' was not found, cannot find configuration values");
        }
        config = mainConfig.get();
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent ede)
    {
        //if we're enabled and a ghast died
        if(isEnabled() && ede.getEntityType() == EntityType.GHAST) {
            //if ghasts can't drop tears in this world
            if(featureEnabledForWorld(ede.getEntity().getWorld().getName())) {

                //get the list of drops
                List<ItemStack> drops = ede.getDrops();

                //for all the items dropped
                Iterator<ItemStack> iterator = drops.iterator();
                Collection<ItemStack> toAdd = new ArrayList<ItemStack>();
                while(iterator.hasNext()) {
                    ItemStack is = iterator.next();
                    //if it was a ghast tear drop the same amount of gold ingots
                    if(is.getType() == Material.GHAST_TEAR) {
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
    public boolean featureEnabledForWorld(String worldName)
    {
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
    public String getFeatureID()
    {
        return "GhastDrops";
    }

    @Override
    public String getDescription()
    {
        return "Ghasts drop golden ingots instead of tears";
    }
}
