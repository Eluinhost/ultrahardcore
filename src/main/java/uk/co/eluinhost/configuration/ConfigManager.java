package uk.co.eluinhost.configuration;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Map<String, FileConfiguration> m_configurations = new HashMap<String, FileConfiguration>();

    private final FileConfiguration m_translation;
    private final Plugin m_plugin;

    public ConfigManager(Plugin plugin){
        m_plugin = plugin;
        FileConfiguration transConfig = getFromFile("translate.yml",true);
        String language = transConfig.getString("language");
        m_translation = getFromFile("translations/"+language+".yml",true);
    }

    /**
     * Get the message, replaces colour codes
     * @param key the key to look for
     * @return the translated message
     */
    public String getMessage(String key){
        return ChatColor.translateAlternateColorCodes('&',m_translation.getString(key));
    }

    /**
     * Only allows 1 config per configtype
     * @param name the name of the config file
     * @param config the config file to save
     */
    public void addConfiguration(String name, FileConfiguration config){
        m_configurations.put(name,config);
    }

    /**
     * Gets a FileConfiguration from the config directory
     * @param path the file to look for
     * @param setDefaults whether to set the defaults from the same file in the jar
     * @return the config file
     */
    public FileConfiguration getFromFile(String path, boolean setDefaults){
        File configFile = new File(m_plugin.getDataFolder(), path);
        if (!configFile.exists()) {
            m_plugin.saveResource(path, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if(setDefaults){
            // Look for defaults in the jar
            InputStream defConfigStream = m_plugin.getResource(path);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                config.setDefaults(defConfig);
            }
        }
        return config;
    }

    /**
     * @param name the name of config to get
     * @return the configuration or null if not exists
     */
    public FileConfiguration getConfig(String name) {
        return m_configurations.get(name);
    }

    /**
     * @return the 'main' config file
     */
    public FileConfiguration getConfig(){
        return getConfig("main");
    }

    /**
     * Saves the config to a file with the same name + .yml
     * @param name the config to save
     */
    public void saveConfig(String name) {
        FileConfiguration config = m_configurations.get(name);
        if(config != null){
            try {
                config.save(name+".yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
