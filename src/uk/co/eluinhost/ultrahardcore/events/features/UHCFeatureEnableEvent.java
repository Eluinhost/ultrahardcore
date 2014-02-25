package uk.co.eluinhost.ultrahardcore.events.features;

import uk.co.eluinhost.ultrahardcore.features.IUHCFeature;

public class UHCFeatureEnableEvent extends UHCFeatureEvent{
    /**
     * Called when a feature is enabled, cancelling stops the feautre enabling
     * @param feature the feautre involved
     */
    public UHCFeatureEnableEvent(IUHCFeature feature) {
        super(feature);
    }
}
