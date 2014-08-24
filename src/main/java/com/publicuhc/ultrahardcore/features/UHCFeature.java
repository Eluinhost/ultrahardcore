/*
 * Feature.java
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

package com.publicuhc.ultrahardcore.features;

import com.publicuhc.ultrahardcore.features.events.FeatureDisableEvent;
import com.publicuhc.ultrahardcore.features.events.FeatureEnableEvent;
import com.publicuhc.ultrahardcore.features.events.FeatureEvent;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

public abstract class UHCFeature implements Feature {

    /**
     * Is the feautre enabeld right now?
     */
    private boolean m_enabled;

    @Override
    public final boolean enableFeature(){
        //if we're already enabled don't do anything
        if (isEnabled())
            return false;

        //trigger an enable event
        FeatureEvent event = new FeatureEnableEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        //if the event was cancelled don't do anything
        if(event.isCancelled())
            return false;

        //enable the feature
        m_enabled = true;
        enableCallback();
        return true;
    }

    @Override
    public final boolean disableFeature(){
        //if we're already disabled don't do anything
        if (!isEnabled())
            return false;

        //trigger a disable event
        FeatureDisableEvent event = new FeatureDisableEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        //if the event was cancelled don't do anything
        if(event.isCancelled())
            return false;

        //disable the feature
        m_enabled = false;
        disableCallback();
        return true;
    }

    /**
     * Called when the feature is being enabled. Override to provide on enable actions.
     */
    protected void enableCallback(){}

    /**
     * Called when the feature is being disabled. Override to provide on disable actions.
     */
    protected void disableCallback(){}

    @Override
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Are the features the same feature? Returns true if they have the same ID
     *
     * @param obj Object
     * @return boolean true if features are equal
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Feature && ((Feature) obj).getFeatureID().equals(getFeatureID());
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getFeatureID()).toHashCode();
    }

    /**
     * @return list of messages to show for this features status when requested
     */
    @Override
    public List<String> getStatus() {
        return null;
    }

    /**
     * Utility method to convert true to green ON and false to red OFF
     *
     * @param onOff the boolean
     * @return green 'ON' if true, red 'OFF' if false
     */
    protected static String convertBooleanToOnOff(boolean onOff) {
        return onOff ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF";
    }
}
