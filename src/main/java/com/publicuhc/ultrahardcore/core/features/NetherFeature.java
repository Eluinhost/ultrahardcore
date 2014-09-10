/*
 * NetherFeature.java
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
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.permissions.Permissible;

/**
 * NetherFeature
 * <p/>
 * Enabled: Disables travelling into the nether
 * Disabled: Nothing
 */
@Singleton
public class NetherFeature extends UHCFeature
{

    public static final String ALLOW_NETHER = "UHC.nether.allow";

    @EventHandler
    public void onPortalEvent(EntityPortalEvent epe)
    {
        //if it's enabled
        if(isEnabled() && epe.getEntity() instanceof Permissible) {
            //if they're going into the nether cancel it
            if(!((Permissible) epe.getEntity()).hasPermission(ALLOW_NETHER)) {
                if(epe.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                    epe.setCancelled(true);
                }
            }
        }
    }

    @Override
    public String getFeatureID()
    {
        return "NetherFeature";
    }

    @Override
    public String getDescription()
    {
        return "Disables the use of nether portals";
    }
}
