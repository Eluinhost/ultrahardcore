/*
 * UHCFeature.java
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

package com.publicuhc.ultrahardcore.api;

import com.publicuhc.ultrahardcore.api.events.FeatureDisableEvent;
import com.publicuhc.ultrahardcore.api.events.FeatureEnableEvent;
import com.publicuhc.ultrahardcore.api.events.FeatureEvent;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public abstract class UHCFeature implements Feature
{

    /**
     * Is the feautre enabeld right now?
     */
    private boolean enabled;

    /**
     * Utility method to convert true to green ON and false to red OFF
     *
     * @param onOff the boolean
     * @return green 'ON' if true, red 'OFF' if false
     */
    protected static String convertBooleanToOnOff(boolean onOff)
    {
        return onOff ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF";
    }

    @Override
    public final boolean enableFeature()
    {
        //if we're already enabled don't do anything
        if(isEnabled()) {
            return false;
        }

        //trigger an enable event
        FeatureEvent event = new FeatureEnableEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        //if the event was cancelled don't do anything
        if(event.isCancelled()) {
            return false;
        }

        //enable the feature
        enabled = true;
        enableCallback();
        return true;
    }

    @Override
    public final boolean disableFeature()
    {
        //if we're already disabled don't do anything
        if(!isEnabled()) {
            return false;
        }

        //trigger a disable event
        FeatureDisableEvent event = new FeatureDisableEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        //if the event was cancelled don't do anything
        if(event.isCancelled()) {
            return false;
        }

        //disable the feature
        enabled = false;
        disableCallback();
        return true;
    }

    /**
     * Called when the feature is being enabled. Override to provide on enable actions.
     */
    protected void enableCallback()
    {}

    /**
     * Called when the feature is being disabled. Override to provide on disable actions.
     */
    protected void disableCallback()
    {}

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Are the features the same feature? Returns true if they have the same ID
     *
     * @param obj Object
     * @return boolean true if features are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Feature && ((Feature) obj).getFeatureID().equals(getFeatureID());
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 31).append(getFeatureID()).toHashCode();
    }

    /**
     * @return list of messages to show for this features status when requested
     */
    @Override
    public List<String> getStatus()
    {
        return new ArrayList<String>();
    }
}
