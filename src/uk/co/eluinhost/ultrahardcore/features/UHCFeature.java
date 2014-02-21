package uk.co.eluinhost.ultrahardcore.features;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.event.Listener;

import uk.co.eluinhost.ultrahardcore.exceptions.FeatureStateNotChangedException;

public abstract class UHCFeature implements Listener {

    /**
     * The feature ID for the feature
     */
    private String m_featureID = null;
    /**
     * Is the feautre enabeld right now?
     */
    private boolean m_enabled = false;
    /**
     * The description of the current feature
     */
    private String m_description = "N/A";

    //TODO refactor to events
    public abstract void enableFeature();

    //TODO refactor to events
    public abstract void disableFeature();

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
     * Set the enabled status of this feature
     * @param enable
     * @throws FeatureStateNotChangedException
     * TODO remove this and put it's details in the manager
     */
    public void setEnabled(boolean enable) throws FeatureStateNotChangedException {
        //if we're changing state
        if (enable != isEnabled()) {
            if (enable) {
                m_enabled = true;
                enableFeature();
            } else {
                m_enabled = false;
                disableFeature();
            }
        } else {
            throw new FeatureStateNotChangedException();
        }
    }

    /**
     * Construct a new feature
     * @param featureID the feature ID to use
     * @param enabled
     * TODO remove the enabled param
     */
    protected UHCFeature(String featureID, boolean enabled) {
        m_featureID = featureID;
        m_enabled = enabled;
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
