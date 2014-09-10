/*
 * FeatureValueConverter.java
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

import com.google.common.base.Optional;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.ValueConversionException;
import com.publicuhc.pluginframework.shaded.joptsimple.ValueConverter;

public class FeatureValueConverter implements ValueConverter<Feature>
{

    private final FeatureManager manager;

    @Inject
    public FeatureValueConverter(FeatureManager manager)
    {
        this.manager = manager;
    }

    @Override
    public Feature convert(String value)
    {
        Optional<Feature> feature = manager.getFeatureByID(value);
        if(feature.isPresent()) {
            throw new ValueConversionException("Invalid feature ID: " + value);
        }
        return feature.get();
    }

    @Override
    public Class<Feature> valueType()
    {
        return Feature.class;
    }

    @Override
    public String valuePattern()
    {
        return "FeatureID";
    }
}
