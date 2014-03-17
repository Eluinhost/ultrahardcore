package uk.co.eluinhost.configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RealConfigManager implements ConfigManager {

    private final Map<String, FileConfiguration> m_configurations = new HashMap<String, FileConfiguration>();

    private final FileConfiguration m_translation;
    private final Plugin m_plugin;

    /**
     * Handles the config files
     * @param plugin the plugin
     */
    @Inject
    private RealConfigManager(Plugin plugin){
        m_plugin = plugin;
        FileConfiguration transConfig = getFromFile("translate.yml",true);
        String language = transConfig.getString("language");
        m_translation = getFromFile("translations/"+language+".yml",true);
    }

    @Override
    public String getMessage(String key){
        return ChatColor.translateAlternateColorCodes('&',m_translation.getString(key));
    }

    @Override
    public void addConfiguration(String name, FileConfiguration config){
        m_configurations.put(name,config);
    }

    @Override
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

    @Override
    public FileConfiguration getConfig(String name) {
        return m_configurations.get(name);
    }

    @Override
    public FileConfiguration getConfig(){
        return getConfig("main");
    }

    @Override
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
