package uk.co.eluinhost.features.events;

import uk.co.eluinhost.features.IFeature;

public class FeatureDisableEvent extends FeatureEvent {
    /**
     * Called when a feature is disabled, cancelling stops the feature disabling
     * @param feature the feature involved
     */
    public FeatureDisableEvent(IFeature feature) {
        super(feature);
    }
}
