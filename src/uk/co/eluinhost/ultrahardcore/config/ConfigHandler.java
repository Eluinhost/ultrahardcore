package uk.co.eluinhost.ultrahardcore.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

//TODO make not utility, cleaner usage
@SuppressWarnings("UtilityClass")
public class ConfigHandler {

    public static final int MAIN = 0;
    public static final int BANS = 1;

    private static final FileConfiguration MAIN_CONFIG;
    private static final FileConfiguration BAN_CONFIG;

    //TODO eww
    static {
        MAIN_CONFIG = UltraHardcore.getInstance().getConfig();
        File banFile = new File(UltraHardcore.getInstance().getDataFolder(), "bans.yml");
        if (!banFile.exists()) {
            UltraHardcore.getInstance().saveResource("bans.yml", false);
        }
        BAN_CONFIG = YamlConfiguration.loadConfiguration(banFile);
        // Look for defaults in the jar
        InputStream defConfigStream = UltraHardcore.getInstance().getResource("bans.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            BAN_CONFIG.setDefaults(defConfig);
        }
    }

    private ConfigHandler() {}

    public static FileConfiguration getConfig(int v) {
        switch (v) {
            case MAIN:
                return MAIN_CONFIG;
            case BANS:
                return BAN_CONFIG;
            default:
                return null;
        }
    }

    public static boolean featureEnabledForWorld(String featureNode, String worldName) {
        //w&&f = enabled
        //w&&!f = disabled
        //!w&&f = disabled
        //!w&&!f = enabled
        // = AND
        boolean whitelist = MAIN_CONFIG.getBoolean(featureNode + ".whitelist");
        boolean found = MAIN_CONFIG.getStringList(featureNode + ".worlds").contains(worldName);
        return !(whitelist ^ found);
    }

    public static void saveConfig(int type) {
        switch (type) {
            case MAIN:
                UltraHardcore.getInstance().saveConfig();
                break;
            case BANS:
                try {
                    BAN_CONFIG.save(new File(UltraHardcore.getInstance().getDataFolder(), "bans.yml"));
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Could not save ban config file. " + ex);
                }
        }
    }
}
