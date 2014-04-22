package com.publicuhc.ultrahardcore.features.timer;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.plugin.Plugin;

public class TimerFeature extends UHCFeature {

    private TimerRunnable m_runnable = null;

    /**
     * Construct a new feature
     *
     * @param plugin        the plugin to use
     * @param configManager the config manager to use
     * @param translate the translator
     */
    @Inject
    private TimerFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * @param ticks number of seconds to run for
     * @param message the message to send
     * @return true if running, false if one already running
     */
    public boolean startTimer(int ticks, String message) {
        if(!isEnabled() || m_runnable != null && m_runnable.isRunning()){
            return false;
        }
        m_runnable = new TimerRunnable(ticks, message, getPlugin());
        m_runnable.start();
        return true;
    }

    public boolean stopTimer(){
        if(m_runnable == null || !m_runnable.isRunning()){
            return false;
        }
        m_runnable.stopTimer();
        return true;
    }

    @Override
    public void disableCallback(){
        stopTimer();
    }


    @Override
    public String getFeatureID() {
        return "Timer";
    }

    @Override
    public String getDescription() {
        return "Enderdragon timers";
    }
}
