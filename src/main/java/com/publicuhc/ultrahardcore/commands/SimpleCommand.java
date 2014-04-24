/*
 * SimpleCommand.java
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

package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;

import java.util.Map;

public class SimpleCommand {

    private final Configurator m_configManager;
    private final Translate m_translate;

    /**
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    protected SimpleCommand(Configurator configManager, Translate translate){
        m_configManager = configManager;
        m_translate = translate;
    }

    /**
     * @return the config manager
     */
    public Configurator getConfigurator(){
        return m_configManager;
    }

    /**
     * @return the translator
     */
    public Translate getTranslator() {
        return m_translate;
    }

    /**
     * Proxy method for the Translate object method
     */
    public String translate(String key, String locale) {
        return m_translate.translate(key, locale);
    }

    /**
     * Proxy method for the Translate object method
     */
    public String translate(String key, String locale, Map<String, String> vars) {
        return m_translate.translate(key, locale, vars);
    }

    /**
     * Proxy method for the Translate object method
     */
    public String translate(String key, String locale, String var, String value) {
        return m_translate.translate(key, locale, var, value);
    }

    /**
     * Proxy method for the Translate object method getLocaleForSender
     */
    /*public String locale(CommandSender sender) {
        return m_translate.getLocaleForSender(sender);
    }*/
}
