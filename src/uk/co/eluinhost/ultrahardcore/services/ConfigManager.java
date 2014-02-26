package uk.co.eluinhost.ultrahardcore.services;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigType;

import java.io.File;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class ConfigManager {

    @SuppressWarnings("UtilityClass")
    private static final class LazyConfigManagerHolder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    /**
     * @return config manager instance
     */
    public static ConfigManager getInstance(){
        return LazyConfigManagerHolder.INSTANCE;
    }

    /**
     * Makes a config manager
     */
    private ConfigManager(){}

    private final Map<ConfigType, FileConfiguration> m_configurations = new EnumMap<ConfigType, FileConfiguration>(ConfigType.class);

    /**
     * Only allows 1 config per configtype
     * @param type the type to add for
     * @param config the config file to save
     */
    public void addConfiguration(ConfigType type, FileConfiguration config){
        m_configurations.put(type,config);
    }

    /**
     * Gets a FileConfiguration from the config directory
     * @param path the file to look for
     * @param setDefaults whether to set the defaults from the same file in the jar
     * @return the config file
     */
    public static FileConfiguration getFromFile(String path, boolean setDefaults){
        UltraHardcore plugin = UltraHardcore.getInstance();
        File configFile = new File(plugin.getDataFolder(), path);
        if (!configFile.exists()) {
            plugin.saveResource(path, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if(setDefaults){
            // Look for defaults in the jar
            InputStream defConfigStream = plugin.getResource(path);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                config.setDefaults(defConfig);
            }
        }
        return config;
    }

    /**
     * @param type the type of config to get
     * @return the configuration or null if not exists
     */
    public FileConfiguration getConfig(ConfigType type) {
        return m_configurations.get(type);
    }

    /**
     * @return the ConfigType.MAIN config file
     */
    public FileConfiguration getConfig(){
        return getConfig(ConfigType.MAIN);
    }

    //TODO wtf is this supposed to be
    public boolean featureEnabledForWorld(String featureNode, String worldName) {
        //w&&f = enabled
        //w&&!f = disabled
        //!w&&f = disabled
        //!w&&!f = enabled
        // = AND
        boolean whitelist = m_configurations.get(ConfigType.MAIN).getBoolean(featureNode + ".whitelist");
        boolean found = m_configurations.get(ConfigType.MAIN).getStringList(featureNode + ".worlds").contains(worldName);
        return !(whitelist ^ found);
    }

    /**
     * Saves the config
     * @param type the config type to save
     */
    public void saveConfig(ConfigType type) {
        FileConfiguration config = m_configurations.get(type);
        if(config != null){
            //TODO thingy to save and stuff
        }
    }
}
