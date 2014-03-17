package uk.co.eluinhost.ultrahardcore.commands;

import uk.co.eluinhost.configuration.ConfigManager;

public class SimpleCommand {

    private final ConfigManager m_configManager;

    public SimpleCommand(ConfigManager configManager){
        m_configManager = configManager;
    }

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
