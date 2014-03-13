package uk.co.eluinhost.ultrahardcore;

import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.commands.CommandHandler;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.ultrahardcore.borders.BorderTypeManager;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;

public class UltraHardcore {

    private final ConfigManager m_configManager;
    private final FeatureManager m_featureManager;
    private final CommandHandler m_commandHandler;
    private final BorderTypeManager m_borderTypeManager;
    private final ScatterManager m_scatterManager;
    private final Plugin m_plugin;

    /**
     * Actual UHC stuff
     * @param plugin the plugin to run for
     * @param configManager the config manager to use
     * @param featureManager the feature manager to use
     * @param commandHandler the command handler to use
     * @param borderTypeManager the border manager to use
     * @param scatterManager the scatter manager to use
     */
    public UltraHardcore(Plugin plugin, ConfigManager configManager, FeatureManager featureManager, CommandHandler commandHandler, BorderTypeManager borderTypeManager, ScatterManager scatterManager){
        m_configManager = configManager;
        m_featureManager = featureManager;
        m_commandHandler = commandHandler;
        m_borderTypeManager = borderTypeManager;
        m_scatterManager = scatterManager;
        m_plugin = plugin;
    }

    public FeatureManager getFeatureManager(){
        return m_featureManager;
    }

    public ConfigManager getConfigManager(){
        return m_configManager;
    }

    public CommandHandler getCommandHandler(){
        return m_commandHandler;
    }

    public ScatterManager getScatterManager() {
        return m_scatterManager;
    }

    public BorderTypeManager getBorderTypeManager() {
        return m_borderTypeManager;
    }
}
