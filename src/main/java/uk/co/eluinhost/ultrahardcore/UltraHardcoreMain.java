package uk.co.eluinhost.ultrahardcore;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.eluinhost.commands.CommandHandler;
import uk.co.eluinhost.ultrahardcore.borders.BorderTypeManager;
import uk.co.eluinhost.ultrahardcore.borders.exceptions.BorderIDConflictException;
import uk.co.eluinhost.ultrahardcore.borders.types.CylinderBorder;
import uk.co.eluinhost.ultrahardcore.borders.types.RoofBorder;
import uk.co.eluinhost.ultrahardcore.borders.types.SquareBorder;
import uk.co.eluinhost.ultrahardcore.commands.*;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.exceptions.FeatureIDConflictException;
import uk.co.eluinhost.features.exceptions.InvalidFeatureIDException;
import uk.co.eluinhost.ultrahardcore.commands.scatter.ScatterCommand;
import uk.co.eluinhost.ultrahardcore.features.playerfreeze.PlayerFreezeFeature;
import uk.co.eluinhost.ultrahardcore.scatter.exceptions.ScatterTypeConflictException;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.ultrahardcore.features.anonchat.AnonChatFeature;
import uk.co.eluinhost.ultrahardcore.features.deathbans.DeathBan;
import uk.co.eluinhost.ultrahardcore.features.deathbans.DeathBansFeature;
import uk.co.eluinhost.ultrahardcore.features.deathdrops.DeathDropsFeature;
import uk.co.eluinhost.ultrahardcore.features.deathlightning.DeathLightningFeature;
import uk.co.eluinhost.ultrahardcore.features.deathmessages.DeathMessagesFeature;
import uk.co.eluinhost.ultrahardcore.features.enderpearls.EnderpearlsFeature;
import uk.co.eluinhost.ultrahardcore.features.footprints.FootprintFeature;
import uk.co.eluinhost.ultrahardcore.features.ghastdrops.GhastDropsFeature;
import uk.co.eluinhost.ultrahardcore.features.goldenheads.GoldenHeadsFeature;
import uk.co.eluinhost.ultrahardcore.features.hardcorehearts.HardcoreHeartsFeature;
import uk.co.eluinhost.ultrahardcore.features.nether.NetherFeature;
import uk.co.eluinhost.ultrahardcore.features.playerheads.PlayerHeadsFeature;
import uk.co.eluinhost.ultrahardcore.features.playerlist.PlayerListFeature;
import uk.co.eluinhost.ultrahardcore.features.portals.PortalsFeature;
import uk.co.eluinhost.ultrahardcore.features.potionnerfs.PotionNerfsFeature;
import uk.co.eluinhost.ultrahardcore.features.recipes.RecipeFeature;
import uk.co.eluinhost.ultrahardcore.features.regen.RegenFeature;
import uk.co.eluinhost.ultrahardcore.features.witchspawns.WitchSpawnsFeature;
import uk.co.eluinhost.metrics.MetricsLite;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.types.EvenCircumferenceType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomCircularType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomSquareType;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * UltraHardcore
 * <p/>
 * Main plugin class, init
 *
 * @author ghowden
 */
public class UltraHardcoreMain extends JavaPlugin implements Listener {

    private UltraHardcore m_instance = null;

    private ConfigManager m_configManager;

    /**
     * @return the current instance of the plugin
     */
    public static UltraHardcoreMain getInstance() {
        return (UltraHardcoreMain) Bukkit.getPluginManager().getPlugin("UltraHardcore");
    }

    //When the plugin gets started
    @Override
    public void onEnable() {
        //register deathbans for serilization
        ConfigurationSerialization.registerClass(DeathBan.class);

        ConfigManager configManager = new ConfigManager(this);
        m_configManager = configManager;
        FeatureManager featureManager = new FeatureManager(configManager,this);
        CommandHandler commandHandler = new CommandHandler(getLogger());
        BorderTypeManager borderTypeManager = new BorderTypeManager();
        ScatterManager scatterManager = new ScatterManager(this, configManager);

        m_instance = new UltraHardcore(this,configManager, featureManager, commandHandler, borderTypeManager, scatterManager);

        //register the bungeecord plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //load all the configs
        loadDefaultConfigurations();
        //load all the features
        loadDefaultFeatures();
        //load all the scatter types
        loadDefaultScatterTypes();
        //load all the commands
        loadDefaultCommands();
        //load the default border types
        loadDefaultBorders();

        //Load all the metric infos
        try {
            MetricsLite met = new MetricsLite(this);
            met.start();
        } catch (IOException ignored) {
        }
    }

    /**
     * Load the default border types
     */
    private void loadDefaultBorders() {
        BorderTypeManager manager = m_instance.getBorderTypeManager();
        try{
            manager.addBorder(new CylinderBorder());
            manager.addBorder(new RoofBorder());
            manager.addBorder(new SquareBorder());
        } catch (BorderIDConflictException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load all the default commands
     */
    private void loadDefaultCommands() {
        CommandHandler commandHandler = m_instance.getCommandHandler();
        Class[] classes = {
                HealCommand.class,
                ClearInventoryCommand.class,
                TPCommand.class,
                FeatureCommand.class,
                TeamCommands.class,
                FeedCommand.class,
                FreezeCommand.class,
                BorderCommand.class,
                DeathBanCommand.class,
                ScatterCommand.class
        };
        for(Class clazz : classes){
            try {
                commandHandler.registerCommands(clazz);
            } catch (@SuppressWarnings("OverlyBroadCatchBlock") Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load all the default features into the feature manager
     */
    @SuppressWarnings("OverlyCoupledMethod")
    public void loadDefaultFeatures() {
        Logger log = Bukkit.getLogger();
        log.info("Loading UHC feature modules...");
        //Load the default features with settings in config
        FeatureManager featureManager = m_instance.getFeatureManager();
        try {
            featureManager.addFeature(new DeathLightningFeature(this,m_configManager));
            featureManager.addFeature(new EnderpearlsFeature(this,m_configManager));
            featureManager.addFeature(new GhastDropsFeature(this,m_configManager));
            featureManager.addFeature(new PlayerHeadsFeature(this,m_configManager));
            featureManager.addFeature(new PlayerListFeature(this,m_configManager));
            featureManager.addFeature(new RecipeFeature(this,m_configManager));
            featureManager.addFeature(new RegenFeature(this,m_configManager));
            featureManager.addFeature(new DeathMessagesFeature(this,m_configManager));
            featureManager.addFeature(new DeathDropsFeature(this,m_configManager));
            featureManager.addFeature(new AnonChatFeature(this,m_configManager));
            featureManager.addFeature(new GoldenHeadsFeature(this,m_configManager));
            featureManager.addFeature(new DeathBansFeature(this,m_configManager));
            featureManager.addFeature(new PotionNerfsFeature(this,m_configManager));
            featureManager.addFeature(new NetherFeature(this,m_configManager));
            featureManager.addFeature(new WitchSpawnsFeature(this,m_configManager));
            featureManager.addFeature(new PortalsFeature(this,m_configManager));
            featureManager.addFeature(new PlayerFreezeFeature(this,m_configManager));

            //load the protocollib features last
            featureManager.addFeature(new HardcoreHeartsFeature(this,m_configManager));
            featureManager.addFeature(new FootprintFeature(this,m_configManager));
        } catch (FeatureIDConflictException ignored) {
            log.severe("A default UHC Feature ID is conflicting, this should never happen!");
        } catch (InvalidFeatureIDException ignored) {
            log.severe("A default UHC feature ID is invalid, this should never happen!");
        } catch (NoClassDefFoundError ignored) {
            log.severe("Couldn't find protocollib for related features, skipping them all.");
        }
    }

    /**
     * Add the default config files
     */
    public void loadDefaultConfigurations(){
        ConfigManager configManager = m_instance.getConfigManager();
        configManager.addConfiguration("main", configManager.getFromFile("main.yml", true));
        configManager.addConfiguration("bans", configManager.getFromFile("bans.yml", true));
        configManager.addConfiguration("words", configManager.getFromFile("words.yml", true));
    }

    /**
     * Load the default scatter types
     */
    public void loadDefaultScatterTypes(){
        ScatterManager scatterManager = m_instance.getScatterManager();
        try {
            scatterManager.addScatterType(new EvenCircumferenceType());
            scatterManager.addScatterType(new RandomCircularType());
            scatterManager.addScatterType(new RandomSquareType());
        } catch (ScatterTypeConflictException ignored) {
            Bukkit.getLogger().severe("Conflict error when loading default scatter types!");
        }
    }
}