/*
 * WitchSpawnsFeature.java
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

import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;


/**
 * WitchSpawns
 *
 * Enabled: Disables witch spawning
 * Disabled: Nothing
 */
@Singleton
public class WitchSpawnsFeature extends UHCFeature {

    /**
     * Whenever a creature spawns
     * @param ese the creaturespawnevent
     */
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent ese) {
        if (isEnabled() && ese.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && ese.getEntity().getType() == EntityType.WITCH) {
            ese.setCancelled(true);
        }
    }

    @Override
    public String getFeatureID() {
        return "WitchSpawns";
    }

    @Override
    public String getDescription() {
        return "Disables natural witch spawns";
    }
}
