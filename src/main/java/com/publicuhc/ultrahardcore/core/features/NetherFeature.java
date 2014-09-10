/*
 * NetherFeature.java
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
