package com.publicuhc.ultrahardcore.commands;

import com.google.inject.Inject;
import com.publicuhc.configuration.ConfigManager;

public class SimpleCommand {

    private final ConfigManager m_configManager;

    /**
     * @param configManager the config manager
     */
    @Inject
    protected SimpleCommand(ConfigManager configManager){
        m_configManager = configManager;
    }

    /**
     * @return the config manager
     */
    public ConfigManager getConfigManager(){
        return m_configManager;
    }

    /**
     * @param key the key to search for
     * @return the translated message
     */
    public String translate(String key){
        return m_configManager.getMessage(key);
    }
}
