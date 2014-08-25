package com.publicuhc.ultrahardcore.api;

import com.publicuhc.pluginframework.shaded.joptsimple.ValueConversionException;
import com.publicuhc.pluginframework.shaded.joptsimple.ValueConverter;

public class FeatureValueConverter implements ValueConverter<Feature> {

    private final FeatureManager manager;

    public FeatureValueConverter(FeatureManager manager)
    {
        this.manager = manager;
    }

    @Override
    public Feature convert(String value) {
        Feature feature = manager.getFeatureByID(value);
        if(null == feature) {
            throw new ValueConversionException("Invalid feature ID: " + value);
        }
        return null;
    }

    @Override
    public Class<Feature> valueType() {
        return Feature.class;
    }

    @Override
    public String valuePattern() {
        return null;
    }
}
