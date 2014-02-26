package uk.co.eluinhost.ultrahardcore.events.features;

import uk.co.eluinhost.ultrahardcore.features.IUHCFeature;

public class UHCFeatureInitEvent extends UHCFeatureEvent{
    /**
     * Called when a feature is first initialized, cancelling stops the feature from getting events and being added to the list
     * @param feature the feature involved
     */
    public UHCFeatureInitEvent(IUHCFeature feature) {
        super(feature);
    }
}
