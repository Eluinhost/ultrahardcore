/*
 * FeatureManager.java
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

import com.publicuhc.ultrahardcore.features.exceptions.FeatureIDConflictException;
import com.publicuhc.ultrahardcore.features.exceptions.FeatureIDNotFoundException;
import com.publicuhc.ultrahardcore.features.exceptions.InvalidFeatureIDException;

import java.util.List;

public interface FeatureManager {

    /**
     * Add a feature to the manager.
     * Features are enabled/disabled by default based on the enabled features in the config file.
     * Feature names must contain no whitespace.
     *
     * @param feature Feature the feature to be added
     * @throws com.publicuhc.ultrahardcore.features.exceptions.FeatureIDConflictException when feature with the same ID already exists
     * @throws com.publicuhc.ultrahardcore.features.exceptions.InvalidFeatureIDException  when the feature has an invalid ID name
     */
    void addFeature(Feature feature) throws FeatureIDConflictException, InvalidFeatureIDException;

    /**
     * Check if a feature is enabled by it's ID
     *
     * @param featureID String the ID to check for
     * @return true if enabled, false otherwise
     * @throws com.publicuhc.ultrahardcore.features.exceptions.FeatureIDNotFoundException when feature not found
     */
    boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException;

    /**
     * Get the Feature based on it's ID
     *
     * @param featureID String the ID to check for
     * @return the returned feature, or null if not found
     */
    Feature getFeatureByID(String featureID);

    /**
     * @return an unmodifiable list of all of the loaded features
     */
    List<Feature> getFeatures();

    /**
     * @return List of all the currently registered feature names
     */
    List<String> getFeatureNames();
}
