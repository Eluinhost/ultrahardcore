package com.publicuhc.ultrahardcore;

import com.publicuhc.pluginframework.FrameworkJavaPlugin;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.pluginfeatures.deathbans.DeathBan;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.mcstats.Metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * UltraHardcore
 * <p/>
 * Main plugin class, init
 *
 * @author ghowden
 */
@Singleton
public class UltraHardcore extends FrameworkJavaPlugin {

    //When the plugin gets started
    @Override
    public void onEnable() {
        //register deathbans for serilization
        ConfigurationSerialization.registerClass(DeathBan.class);
        //register the bungeecord plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    /**
     * Load all the defaults for the entire plugin, DefaultClasses will do this when being created so we just print that it is done
     * @param defaultClasses init class
     */
    @Inject
    public void loadDefaultClasses(DefaultClasses defaultClasses) {
        defaultClasses.loadDefaultCommands();
        if(Bukkit.getPluginManager().getPlugin("WorldEdit") == null){
            defaultClasses.loadBorders();
        }
        getLogger().log(Level.INFO, "All default classes loaded");
    }

    /**
     * Load the metrics class and start it
     * @param metrics the metrics to use
     */
    @Inject
    public void loadMetrics(Metrics metrics) {
        metrics.start();
    }

    @Override
    public List<AbstractModule> initialModules() {
        List<AbstractModule> customModules = new ArrayList<AbstractModule>();
        customModules.add(new UHCModule());
        return customModules;
    }
}