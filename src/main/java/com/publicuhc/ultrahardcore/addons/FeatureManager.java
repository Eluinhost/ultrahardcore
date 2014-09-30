/*
 * FeatureManager.java
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

package com.publicuhc.ultrahardcore.addons;

import com.google.common.base.Optional;
import com.publicuhc.ultrahardcore.api.Feature;
import com.publicuhc.ultrahardcore.api.exceptions.FeatureIDConflictException;
import com.publicuhc.ultrahardcore.api.exceptions.FeatureIDNotFoundException;

import java.util.Collection;
import java.util.List;

public interface FeatureManager
{

    /**
     * Add a feature to the manager.
     * Features are enabled/disabled by default based on the enabled features in the config file.
     * Feature names must contain no whitespace.
     *
     * @param feature Feature the feature to be added
     * @throws com.publicuhc.ultrahardcore.api.exceptions.FeatureIDConflictException when feature with the same ID already exists
     * @throws java.lang.IllegalArgumentException                                    when the feature has an invalid ID name
     */
    void addFeature(Feature feature) throws FeatureIDConflictException;

    /**
     * Check if a feature is enabled by it's ID
     *
     * @param featureID String the ID to check for
     * @return true if enabled, false otherwise
     * @throws com.publicuhc.ultrahardcore.api.exceptions.FeatureIDNotFoundException when feature not found
     */
    boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException;

    /**
     * Check if a feature is enabled by it's class
     *
     * @param type the class to check for
     * @return true if enabled, false otherwise
     * @throws com.publicuhc.ultrahardcore.api.exceptions.FeatureIDNotFoundException when feature not found
     */
    boolean isFeatureEnabled(Class<? extends Feature> type) throws FeatureIDNotFoundException;

    /**
     * Get the Feature based on it's ID
     *
     * @param featureID String the ID to check for
     * @return the returned feature
     */
    Optional<Feature> getFeatureByID(String featureID);

    /**
     * Get the Feature based on the class
     *
     * @param type
     * @return the feature if it exists
     */
    <T extends Feature> Optional<T> getFeature(Class<T> type);

    /**
     * @return an unmodifiable collection of all of the loaded features
     */
    Collection<Feature> getFeatures();

    /**
     * @return List of all the currently registered feature names
     */
    List<String> getFeatureNames();
}
