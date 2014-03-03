package uk.co.eluinhost.ultrahardcore.events.features;

import uk.co.eluinhost.features.IFeature;

public class UHCFeatureEnableEvent extends UHCFeatureEvent{
    /**
     * Called when a feature is enabled, cancelling stops the feautre enabling
     * @param feature the feautre involved
     */
    public UHCFeatureEnableEvent(IFeature feature) {
        super(feature);
    }
}
