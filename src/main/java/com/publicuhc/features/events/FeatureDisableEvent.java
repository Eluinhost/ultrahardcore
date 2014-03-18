package com.publicuhc.features.events;

import com.publicuhc.features.IFeature;

public class FeatureDisableEvent extends FeatureEvent {
    /**
     * Called when a feature is disabled, cancelling stops the feature disabling
     * @param feature the feature involved
     */
    public FeatureDisableEvent(IFeature feature) {
        super(feature);
    }
}
