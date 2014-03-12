package uk.co.eluinhost.ultrahardcore.commands;

import uk.co.eluinhost.configuration.ConfigManager;

public class SimpleCommand {

    private final ConfigManager m_configManager = ConfigManager.getInstance();

    /**
     * @param key the key to search for
     * @return the translated message
     */
    public String translate(String key){
        return m_configManager.getMessage(key);
    }
}
