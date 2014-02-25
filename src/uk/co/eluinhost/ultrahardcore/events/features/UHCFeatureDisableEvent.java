package uk.co.eluinhost.ultrahardcore.events.features;

import uk.co.eluinhost.ultrahardcore.features.IUHCFeature;

public class UHCFeatureDisableEvent extends UHCFeatureEvent{
    /**
     * Called when a feature is disabled, cancelling stops the feature disabling
     * @param feature the feature involved
     */
    public UHCFeatureDisableEvent(IUHCFeature feature) {
        super(feature);
    }
}
