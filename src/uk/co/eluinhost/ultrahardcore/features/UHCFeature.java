package uk.co.eluinhost.ultrahardcore.features;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.event.Listener;

import uk.co.eluinhost.ultrahardcore.exceptions.FeatureStateNotChangedException;
import uk.co.eluinhost.ultrahardcore.features.events.UHCFeatureDisableEvent;
import uk.co.eluinhost.ultrahardcore.features.events.UHCFeatureEnableEvent;

public class UHCFeature implements Listener {

    /**
     * The feature ID for the feature
     */
    private final String m_featureID;
    /**
     * Is the feautre enabeld right now?
     */
    private boolean m_enabled;
    /**
     * The description of the current feature
     */
    private String m_description = "N/A";

    /**
     * Attempt to enable the feature
     * @return bool true if the feature was enabled, false if already enabled or event cancelled
     */
    public final boolean enableFeature(){
        if(isEnabled()){
            return false;
        }
        UHCFeatureEnableEvent event = new UHCFeatureEnableEvent(this);
        if(event.isAllowed()){
            m_enabled = true;
        }
        return true;
    }

    /**
     * Attempt to disable the feature
     * @return bool true if the feature was disabled, false if already disabled or event cancelled
     */
    public final boolean disableFeature(){
        if(!isEnabled()){
            return false;
        }
        UHCFeatureDisableEvent event = new UHCFeatureDisableEvent(this);
        if(event.isAllowed()){
            m_enabled = false;
        }
        return true;
    }

    protected void enableCallback(){}
    protected void disableCallback(){}

    /**
     * Get the name of the current feature
     * @return String
     */
    public String getFeatureID() {
        return m_featureID;
    }

    /**
     * Is the feature enabled?
     * @return boolean
     */
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Construct a new feature
     * @param featureID the feature ID to use
     * TODO move description here
     */
    protected UHCFeature(String featureID) {
        m_featureID = featureID;
    }

    /**
     * Are the features the same feature? Returns true if they have the same ID
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof UHCFeature && ((UHCFeature) obj).getFeatureID().equals(getFeatureID());
    }

    /**
     * Return the hashcode of this feature
     * @return int
     */
    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getFeatureID()).toHashCode();
    }

    /**
     * Get the description of the feature
     * @return String
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * Set the description of the feature
     * TODO remove this feature, put in constructor
     * @param description String
     */
    public void setDescription(String description) {
        m_description = description;
    }
}
