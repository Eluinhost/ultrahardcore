package uk.co.eluinhost.ultrahardcore;

import com.google.inject.AbstractModule;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.commands.CommandHandler;
import uk.co.eluinhost.commands.CommandMap;
import uk.co.eluinhost.commands.RealCommandHandler;
import uk.co.eluinhost.commands.RealCommandMap;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.configuration.RealConfigManager;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.features.RealFeatureManager;
import uk.co.eluinhost.ultrahardcore.commands.FeatureCommand;

import java.util.logging.Logger;

public class UHCModule extends AbstractModule {

    private final Plugin m_plugin;

    /**
     * Guice bindings
     * @param plugin the plugin to run for
     */
    public UHCModule(Plugin plugin){
        m_plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(m_plugin);
        bind(Logger.class).toInstance(m_plugin.getLogger());
        bind(CommandHandler.class).to(RealCommandHandler.class);
        bind(CommandMap.class).to(RealCommandMap.class);
        bind(ConfigManager.class).to(RealConfigManager.class);
        bind(FeatureManager.class).to(RealFeatureManager.class);
    }
}
