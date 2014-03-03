package uk.co.eluinhost.ultrahardcore.events.features;

import uk.co.eluinhost.features.IFeature;

public class UHCFeatureDisableEvent extends UHCFeatureEvent{
    /**
     * Called when a feature is disabled, cancelling stops the feature disabling
     * @param feature the feature involved
     */
    public UHCFeatureDisableEvent(IFeature feature) {
        super(feature);
    }
}
