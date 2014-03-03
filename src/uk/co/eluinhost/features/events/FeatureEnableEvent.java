package uk.co.eluinhost.features.events;

import uk.co.eluinhost.features.IFeature;

public class FeatureEnableEvent extends FeatureEvent {
    /**
     * Called when a feature is enabled, cancelling stops the feautre enabling
     * @param feature the feautre involved
     */
    public FeatureEnableEvent(IFeature feature) {
        super(feature);
    }
}
