package com.publicuhc.ultrahardcore;

import com.google.inject.AbstractModule;
import com.publicuhc.metrics.UHCMetrics;
import org.bukkit.plugin.Plugin;
import com.publicuhc.commands.CommandHandler;
import com.publicuhc.commands.CommandMap;
import com.publicuhc.commands.RealCommandHandler;
import com.publicuhc.commands.RealCommandMap;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.configuration.RealConfigManager;
import com.publicuhc.features.FeatureManager;
import com.publicuhc.features.RealFeatureManager;
import com.publicuhc.ultrahardcore.borders.BorderTypeManager;
import com.publicuhc.ultrahardcore.borders.RealBorderTypeManager;
import com.publicuhc.ultrahardcore.scatter.FallProtector;
import com.publicuhc.ultrahardcore.scatter.Protector;
import com.publicuhc.ultrahardcore.scatter.RealScatterManager;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import org.mcstats.Metrics;

public class UHCModule extends AbstractModule {

    private final Plugin m_plugin;

    /**
     * Guice bindings
     * @param plugin the plugin to run for
     */
    public UHCModule(Plugin plugin){
        m_plugin = plugin;
    }

    @SuppressWarnings("OverlyCoupledMethod")
    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(m_plugin);
        //bind(Logger.class).toInstance(m_plugin.getLogger());
        bind(CommandMap.class).to(RealCommandMap.class);
        bind(CommandHandler.class).to(RealCommandHandler.class);
        bind(ConfigManager.class).to(RealConfigManager.class);
        bind(FeatureManager.class).to(RealFeatureManager.class);
        bind(BorderTypeManager.class).to(RealBorderTypeManager.class);
        bind(Protector.class).to(FallProtector.class);
        bind(ScatterManager.class).to(RealScatterManager.class);
        bind(Metrics.class).to(UHCMetrics.class);
    }
}
