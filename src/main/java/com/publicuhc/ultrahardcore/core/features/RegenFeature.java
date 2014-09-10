/*
 * RegenFeature.java
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

import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;


/**
 * RegenFeature
 * <p/>
 * Enabled: Disables health regen for players
 * Disabled: Nothing
 */
@Singleton
public class RegenFeature extends UHCFeature
{

    public static final String NO_HEALTH_REGEN = "UHC.disableRegen";
    private static final int FOOD_LEVEL = 18;
    private static final double PLAYER_DEAD_HEALTH = 0.0;
    private static final float EXHAUSTION_OFFSET = 3.0F;

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent erhe)
    {
        if(!isEnabled()) {
            return;
        }

        //If its a player regen
        if(erhe.getEntityType() != EntityType.PLAYER) {
            return;
        }


        //If its just standard health regen
        if(erhe.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }

        Player p = (Player) erhe.getEntity();

        if(p.hasPermission(NO_HEALTH_REGEN)) {

            //this is a special addition due to a bukkit bug where it adds exhuastion even if the healing is cancelled
            if(p.getFoodLevel() >= FOOD_LEVEL && p.getHealth() > PLAYER_DEAD_HEALTH && p.getHealth() < p.getMaxHealth()) {
                p.setExhaustion(p.getExhaustion() - EXHAUSTION_OFFSET);
            }

            //Cancel the event to stop the regen
            erhe.setCancelled(true);
        }
    }

    @Override
    public String getFeatureID()
    {
        return "DisableRegen";
    }

    @Override
    public String getDescription()
    {
        return "Cancels a player's passive health regeneration";
    }
}
