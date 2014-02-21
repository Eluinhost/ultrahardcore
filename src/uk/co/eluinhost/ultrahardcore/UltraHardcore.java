package uk.co.eluinhost.ultrahardcore;

import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.eluinhost.ultrahardcore.features.core.entity.DeathBan;
import uk.co.eluinhost.ultrahardcore.borders.BorderCreator;
import uk.co.eluinhost.ultrahardcore.commands.*;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.features.FeatureManager;
import uk.co.eluinhost.ultrahardcore.metrics.MetricsLite;

/**
 * UltraHardcore
 * <p/>
 * Main plugin class, init
 *
 * @author ghowden
 */
public class UltraHardcore extends JavaPlugin implements Listener {

    private final FeatureManager m_featureManager = new FeatureManager();

    //get the current plugin
    public static UltraHardcore getInstance() {
        return (UltraHardcore) Bukkit.getServer().getPluginManager().getPlugin("UltraHardcore");
    }

    public FeatureManager getFeatureManager(){
        return m_featureManager;
    }

    //When the plugin gets started
    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(DeathBan.class);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        m_featureManager.loadDefaultModules();
        setupCommands();

        //Load all the metric infos
        try {
            MetricsLite met = new MetricsLite(this);
            met.start();
        } catch (IOException ignored) {
        }
    }

    private void setExecutor(String commandName, UHCCommand ce) {
        PluginCommand pc = getCommand(commandName);
        if (pc == null) {
            getLogger().warning("Plugin failed to register the command " + commandName + ", is the command already taken?");
        } else {
            pc.setExecutor(ce);
            pc.setTabCompleter(ce);
        }
    }

    //TODO use a command handler class to do this
    private void setupCommands() {
        setExecutor("heal", new HealCommand());
        setExecutor("feed", new FeedCommand());
        setExecutor("tpp", new TPCommand());
        setExecutor("ci", new ClearInventoryCommand());
        setExecutor("deathban", new DeathBanCommand());

        TeamCommands tc = new TeamCommands();
        setExecutor("randomteams", tc);
        setExecutor("clearteams", tc);
        setExecutor("listteams", tc);
        setExecutor("createteam", tc);
        setExecutor("removeteam", tc);
        setExecutor("jointeam", tc);
        setExecutor("leaveteam", tc);
        setExecutor("emptyteams", tc);

        setExecutor("scatter", new ScatterCommandConversational());
        setExecutor("freeze", new FreezeCommand());
        setExecutor("feature", new FeatureCommand());
        setExecutor("generateborder", new BorderCreator());
        setExecutor("givedrops", new GiveDropCommand());
        try {
            setExecutor("timer", new TimerCommand());
        } catch (NoClassDefFoundError e) {
            getLogger().severe("Cannot find a class for timer command, ProtocolLib is needed for this feature to work, disabling...");
        }

        for (Field field : PermissionNodes.class.getDeclaredFields()) {
            try {
                Object o = field.get(PermissionNodes.class);
                if (o instanceof Permission) {
                    getServer().getPluginManager().addPermission((Permission) o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}