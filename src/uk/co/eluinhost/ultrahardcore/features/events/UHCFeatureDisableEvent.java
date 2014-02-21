package uk.co.eluinhost.ultrahardcore.features.events;

import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class UHCFeatureDisableEvent extends UHCFeatureEvent{
    public UHCFeatureDisableEvent(UHCFeature feature) {
        super(feature);
    }
}
