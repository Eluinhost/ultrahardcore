package uk.co.eluinhost.ultrahardcore.features;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

import uk.co.eluinhost.ultrahardcore.events.features.UHCFeatureDisableEvent;
import uk.co.eluinhost.ultrahardcore.events.features.UHCFeatureEnableEvent;
import uk.co.eluinhost.ultrahardcore.events.features.UHCFeatureEvent;

public class UHCFeature implements Listener, IUHCFeature {

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
    private final String m_description;

    @Override
    public boolean enableFeature(){
        if(isEnabled()){
            return false;
        }
        UHCFeatureEvent event = new UHCFeatureEnableEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            m_enabled = true;
            enableCallback();
        }
        return true;
    }

    @Override
    public boolean disableFeature(){
        if(!isEnabled()){
            return false;
        }
        UHCFeatureDisableEvent event = new UHCFeatureDisableEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            m_enabled = false;
            disableCallback();
        }
        return true;
    }

    /**
     * Called when the feature is being enabled
     */
    protected void enableCallback(){}

    /**
     * Called when the feature is being disabled
     */
    protected void disableCallback(){}

    @Override
    public String getFeatureID() {
        return m_featureID;
    }


    @Override
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Construct a new feature
     * @param featureID the feature ID to use
     * @param description the description for the feature
     */
    protected UHCFeature(String featureID,String description) {
        m_featureID = featureID;
        m_description = description;
    }

    /**
     * Are the features the same feature? Returns true if they have the same ID
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IUHCFeature && ((IUHCFeature) obj).getFeatureID().equals(getFeatureID());
    }

    /**
     * Return the hashcode of this feature
     * @return int
     */
    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getFeatureID()).toHashCode();
    }

    @Override
    public String getDescription() {
        return m_description;
    }
}
