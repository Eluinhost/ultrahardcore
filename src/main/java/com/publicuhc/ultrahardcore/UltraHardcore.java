package com.publicuhc.ultrahardcore;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.publicuhc.metrics.MetricsLite;
import com.publicuhc.ultrahardcore.features.deathbans.DeathBan;

import java.io.IOException;

/**
 * UltraHardcore
 * <p/>
 * Main plugin class, init
 *
 * @author ghowden
 */
@Singleton
public class UltraHardcore extends JavaPlugin implements Listener {

    /**
     * @return the current instance of the plugin
     */
    public static Plugin getInstance() {
        return Bukkit.getPluginManager().getPlugin("UltraHardcore");
    }

    //When the plugin gets started
    @Override
    public void onEnable() {
        //register deathbans for serilization
        ConfigurationSerialization.registerClass(DeathBan.class);
        //register the bungeecord plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Injector injector = Guice.createInjector(new UHCModule(this));

        DefaultClasses defaults = injector.getInstance(DefaultClasses.class);

        //load all the configs
        defaults.loadDefaultConfigurations();
        //load all the features
        defaults.loadDefaultFeatures(injector);
        //load all the scatter types
        defaults.loadDefaultScatterTypes(injector);
        //load all the commands
        defaults.loadDefaultCommands();

        if(Bukkit.getPluginManager().getPlugin("WorldEdit") != null){
        //load the default border types
        defaults.loadDefaultBorders();
        }

        //Load all the metric infos
        try {
            MetricsLite met = new MetricsLite(this);
            met.start();
        } catch (IOException ignored) {
        }
    }
}