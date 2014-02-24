package uk.co.eluinhost.ultrahardcore.services;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigType;

import java.io.File;
import java.io.InputStream;
import java.util.EnumMap;

public class ConfigManager {

   private final EnumMap<ConfigType, FileConfiguration> m_configurations = new EnumMap<ConfigType, FileConfiguration>(ConfigType.class);

    /**
     * Only allows 1 config per configtype
     * @param type the type to add for
     * @param config the config file to save
     */
    public void addConfiguration(ConfigType type, FileConfiguration config){
        m_configurations.put(type,config);
    }

    /**
     * Add the default config files
     */
    public void addDefaults(){
        addConfiguration(ConfigType.MAIN, getFromFile("main.yml",true));
        addConfiguration(ConfigType.BANS, getFromFile("bans.yml",true));
    }

    /**
     * Gets a FileConfiguration from the config directory
     * @param path the file to look for
     * @param setDefaults whether to set the defaults from the same file in the jar
     * @return the config file
     */
    public FileConfiguration getFromFile(String path, boolean setDefaults){
        File configFile = new File(UltraHardcore.getInstance().getDataFolder(), path);
        if (!configFile.exists()) {
            UltraHardcore.getInstance().saveResource(path, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if(setDefaults){
            // Look for defaults in the jar
            InputStream defConfigStream = UltraHardcore.getInstance().getResource(path);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                config.setDefaults(defConfig);
            }
        }
        return config;
    }

    public FileConfiguration getConfig(ConfigType type) {
        return m_configurations.get(type);
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

    public void saveConfig(ConfigType type) {
        FileConfiguration config = m_configurations.get(type);
        if(config != null){
            //TODO thingy to save and stuff
        }
    }
}
