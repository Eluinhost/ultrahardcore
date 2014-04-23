package com.publicuhc.ultrahardcore.features.events;

import com.publicuhc.ultrahardcore.features.IFeature;

public class FeatureInitEvent extends FeatureEvent {
    /**
     * Called when a feature is first initialized, cancelling stops the feature from getting events and being added to the list
     * @param feature the feature involved
     */
    public FeatureInitEvent(IFeature feature) {
        super(feature);
    }
}
