/*
 * IFeature.java
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

package com.publicuhc.ultrahardcore.api;

import org.bukkit.event.Listener;

import java.util.List;

public interface Feature extends Listener {

    /**
    * Attempt to enable the feature
     *
    * @return bool true if the feature was enabled, false if already enabled or event cancelled
    */
    boolean enableFeature();

    /**
     * Attempt to disable the feature
     *
     * @return bool true if the feature was disabled, false if already disabled or event cancelled
     */
    boolean disableFeature();

    /**
     * @return name of the feature
     */
    String getFeatureID();

    /**
     * Is the feature enabled?
     *
     * @return boolean
     */
    boolean isEnabled();

    /**
     * Get the description of the feature
     *
     * @return String
     */
    String getDescription();

    /**
     * @return a list detailing the components of this feature that are enabled/disabled.
     * Return null/empty for no components
     */
    List<String> getStatus();
}
