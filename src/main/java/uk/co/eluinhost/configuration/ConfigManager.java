package uk.co.eluinhost.configuration;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigManager {

    /**
     * Get the message, replaces colour codes
     * @param key the key to look for
     * @return the translated message
     */
    String getMessage(String key);

    /**
     * Only allows 1 config per configtype
     * @param name the name of the config file
     * @param config the config file to save
     */
    void addConfiguration(String name, FileConfiguration config);

    /**
     * Gets a FileConfiguration from the config directory
     * @param path the file to look for
     * @param setDefaults whether to set the defaults from the same file in the jar
     * @return the config file
     */
    FileConfiguration getFromFile(String path, boolean setDefaults);

    /**
     * @param name the name of config to get
     * @return the configuration or null if not exists
     */
    FileConfiguration getConfig(String name);

    /**
     * @return the 'main' config file
     */
    FileConfiguration getConfig();

    /**
     * Saves the config to a file with the same name + .yml
     * @param name the config to save
     */
    void saveConfig(String name);
}
