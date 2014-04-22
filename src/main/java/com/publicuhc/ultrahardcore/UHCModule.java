package com.publicuhc.ultrahardcore;

import com.publicuhc.commands.CommandHandler;
import com.publicuhc.commands.CommandMap;
import com.publicuhc.commands.RealCommandHandler;
import com.publicuhc.commands.RealCommandMap;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.configuration.RealConfigManager;
import com.publicuhc.features.FeatureManager;
import com.publicuhc.features.RealFeatureManager;
import com.publicuhc.metrics.UHCMetrics;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.ultrahardcore.borders.BorderTypeManager;
import com.publicuhc.ultrahardcore.borders.RealBorderTypeManager;
import com.publicuhc.ultrahardcore.scatter.FallProtector;
import com.publicuhc.ultrahardcore.scatter.Protector;
import com.publicuhc.ultrahardcore.scatter.RealScatterManager;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import org.mcstats.Metrics;

@SuppressWarnings("OverlyCoupledMethod")
public class UHCModule extends AbstractModule {

    @Override
    protected void configure() {
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
