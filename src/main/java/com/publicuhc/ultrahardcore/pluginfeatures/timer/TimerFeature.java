/*
 * TimerFeature.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.pluginfeatures.timer;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
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
