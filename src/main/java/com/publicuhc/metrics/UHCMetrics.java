package com.publicuhc.metrics;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.mcstats.Metrics;

import java.io.IOException;

@Singleton
public class UHCMetrics extends Metrics {

    /**
     * Construct some UHC metrics
     * @param plugin the plugin to use
     * @throws IOException when fails or something
     */
    @Inject
    private UHCMetrics(Plugin plugin) throws IOException {
        super(plugin);
    }
}
