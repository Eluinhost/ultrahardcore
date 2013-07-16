package uk.co.eluinhost.UltraHardcore.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class ConfigHandler {

    public static final int MAIN = 0;
    public static final int BANS = 1;

	private static FileConfiguration main_config;
    private static FileConfiguration ban_config;

    static{
        main_config = UltraHardcore.getInstance().getConfig();
        File ban_file = new File(UltraHardcore.getInstance().getDataFolder(),"bans.yml");
        if(!ban_file.exists()){
            UltraHardcore.getInstance().saveResource("bans.yml",false);
        }
        ban_config = YamlConfiguration.loadConfiguration(ban_file);
        // Look for defaults in the jar
        InputStream defConfigStream = UltraHardcore.getInstance().getResource("bans.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            ban_config.setDefaults(defConfig);
        }
    }

    public static FileConfiguration getConfig(int v){
        switch (v){
            case MAIN:
                return main_config;
            case BANS:
                return ban_config;
            default:
                return null;
        }
    }

	public static boolean featureEnabledForWorld(String featureNode, String worldName){
		//w&&f = enabled
		//w&&!f = disabled
		//!w&&f = disabled
		//!w&&!f = enabled
		// = AND
		boolean whitelist = main_config.getBoolean(featureNode+".whitelist");
		boolean found = main_config.getStringList(featureNode+".worlds").contains(worldName);
		return !(whitelist ^ found);
	}

    public static void saveConfig(int type){
        switch (type){
            case MAIN:
                UltraHardcore.getInstance().saveConfig();
            case BANS:
                try {
                    ban_config.save(new File(UltraHardcore.getInstance().getDataFolder(),"bans.yml"));
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Could not save ban config file. "+ex);
                }
        }
    }
}
