/*
 * RegenFeature.java
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;


/**
 * RegenFeature
 *
 * Enabled: Disables health regen for players
 * Disabled: Nothing
 */
@Singleton
public class RegenFeature extends UHCFeature {

    private static final int FOOD_LEVEL = 18;
    private static final double PLAYER_DEAD_HEALTH = 0.0;
    private static final float EXHAUSTION_OFFSET = 3.0F;

    public static final String NO_HEALTH_REGEN = "UHC.disableRegen";

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent erhe) {
        if (!isEnabled())
            return;

        //If its a player regen
        if (erhe.getEntityType() != EntityType.PLAYER)
            return;


        //If its just standard health regen
        if (erhe.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED)
            return;

        Player p = (Player) erhe.getEntity();

        if (p.hasPermission(NO_HEALTH_REGEN)) {

            //this is a special addition due to a bukkit bug where it adds exhuastion even if the healing is cancelled
            if (p.getFoodLevel() >= FOOD_LEVEL && p.getHealth() > PLAYER_DEAD_HEALTH && p.getHealth() < p.getMaxHealth()) {
                p.setExhaustion(p.getExhaustion() - EXHAUSTION_OFFSET);
            }

            //Cancel the event to stop the regen
            erhe.setCancelled(true);
        }
    }

    @Override
    public String getFeatureID() {
        return "DisableRegen";
    }

    @Override
    public String getDescription() {
        return "Cancels a player's passive health regeneration";
    }
}
