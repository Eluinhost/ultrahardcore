package uk.co.eluinhost.features;

import com.google.inject.Inject;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.events.FeatureDisableEvent;
import uk.co.eluinhost.features.events.FeatureEnableEvent;
import uk.co.eluinhost.features.events.FeatureEvent;

public abstract class Feature implements Listener, IFeature {

    /**
     * Is the feautre enabeld right now?
     */
    private boolean m_enabled;

    private final ConfigManager m_configManager;
    private final Plugin m_plugin;

    @Override
    public boolean enableFeature(){
        if(isEnabled()){
            return false;
        }
        FeatureEvent event = new FeatureEnableEvent(this);
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
        FeatureDisableEvent event = new FeatureDisableEvent(this);
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
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Construct a new feature
     * @param plugin the plugin
     * @param configManager the config manager to use
     */
    @Inject
    protected Feature(Plugin plugin, ConfigManager configManager) {
        m_configManager = configManager;
        m_plugin = plugin;
    }

    /**
     * Are the features the same feature? Returns true if they have the same ID
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IFeature && ((IFeature) obj).getFeatureID().equals(getFeatureID());
    }

    /**
     * @return the config manager for the feature
     */
    protected ConfigManager getConfigManager(){
        return m_configManager;
    }

    /**
     * @return the plugin for the feature
     */
    protected Plugin getPlugin(){
        return m_plugin;
    }

    /**
     * Return the hashcode of this feature
     * @return int
     */
    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getFeatureID()).toHashCode();
    }
}
