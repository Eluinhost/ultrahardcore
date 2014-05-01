/*
 * DeathLightningFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.deathlightning;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;


/**
 * DeathLightningFeature
 * <p/>
 * hits people with lightning on death
 *
 * @author ghowden
 */
@Singleton
public class DeathLightningFeature extends UHCFeature {

    public static final String DEATH_LIGHTNING = BASE_PERMISSION + "deathLightning";

    /**
     * Strikes lightning on a player death
     * @param configManager the config manager
     * @param plugin the plugin
     * @param translate the translator
     */
    @Inject
    private DeathLightningFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin,configManager, translate);
    }

    /**
     * Whenever a player dies
     * @param pde the death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        if (isEnabled()) {
            if (pde.getEntity().hasPermission(DEATH_LIGHTNING)) {
                pde.getEntity().getWorld().strikeLightningEffect(pde.getEntity().getLocation());
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "DeathLightning";
    }

    @Override
    public String getDescription() {
        return "Fake lightning on a player's corpse";
    }
}
