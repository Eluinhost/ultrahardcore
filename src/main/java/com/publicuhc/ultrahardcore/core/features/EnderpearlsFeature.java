/*
 * EnderpearlsFeature.java
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.permissions.Permissible;


/**
 * EnderpearlsFeature
 * <p/>
 * Enabled: Removes damage from enderpearls
 * Disabled: Nothing
 * <p/>
 * Permissions: UHC.enderpearls.damage - takes damage from enderpearls when enabled
 */
@Singleton
public class EnderpearlsFeature extends UHCFeature
{

    public static final String ENDERPEARL_DAMAGE = "UHC.enderpearls.damage";

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent ede)
    {
        if(isEnabled()) {
            if(ede.getDamager().getType() == EntityType.ENDER_PEARL) {
                if(!((Permissible) ede.getEntity()).hasPermission(ENDERPEARL_DAMAGE)) {
                    ede.setCancelled(true);
                }
            }
        }
    }

    @Override
    public String getFeatureID()
    {
        return "Enderpearls";
    }

    @Override
    public String getDescription()
    {
        return "Enderpearls cause no teleport damage";
    }
}
