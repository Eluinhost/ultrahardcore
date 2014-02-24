package uk.co.eluinhost.ultrahardcore.events.features;

import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class UHCFeatureEnableEvent extends UHCFeatureEvent{
    public UHCFeatureEnableEvent(UHCFeature feature) {
        super(feature);
    }
}
