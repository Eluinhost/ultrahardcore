package uk.co.eluinhost.ultrahardcore;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.config.ConfigType;
import uk.co.eluinhost.ultrahardcore.exceptions.features.FeatureIDConflictException;
import uk.co.eluinhost.ultrahardcore.exceptions.features.InvalidFeatureIDException;
import uk.co.eluinhost.ultrahardcore.exceptions.scatter.ScatterTypeConflictException;
import uk.co.eluinhost.ultrahardcore.features.anonchat.AnonChatFeature;
import uk.co.eluinhost.ultrahardcore.features.deathbans.DeathBan;
import uk.co.eluinhost.ultrahardcore.commands.*;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
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
import uk.co.eluinhost.ultrahardcore.scatter.types.EvenCircumferenceType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomCircularType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomSquareType;
import uk.co.eluinhost.ultrahardcore.services.ConfigManager;
import uk.co.eluinhost.ultrahardcore.services.FeatureManager;
import uk.co.eluinhost.ultrahardcore.metrics.MetricsLite;
import uk.co.eluinhost.ultrahardcore.services.ScatterManager;

/**
 * UltraHardcore
 * <p/>
 * Main plugin class, init
 *
 * @author ghowden
 */
public class UltraHardcore extends JavaPlugin implements Listener {

    /**
     * @return the current instance of the plugin
     */
    public static UltraHardcore getInstance() {
        return (UltraHardcore) Bukkit.getServer().getPluginManager().getPlugin("UltraHardcore");
    }

    //When the plugin gets started
    @Override
    public void onEnable() {
        //register deathbans for serilization
        ConfigurationSerialization.registerClass(DeathBan.class);

        //register the bungeecord plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //load all the configs
        loadDefaultConfigurations();
        //load all the features
        loadDefaultFeatures();
        //load all the scatter types
        loadDefaultScatterTypes();
        //load all the commands

        setupCommands();

        //Load all the metric infos
        try {
            MetricsLite met = new MetricsLite(this);
            met.start();
        } catch (IOException ignored) {
        }
    }

    //TODO command handler stuff
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

        UHCCommand teamCommands = new TeamCommands();
        setExecutor("randomteams", teamCommands);
        setExecutor("clearteams", teamCommands);
        setExecutor("listteams", teamCommands);
        setExecutor("createteam", teamCommands);
        setExecutor("removeteam", teamCommands);
        setExecutor("jointeam", teamCommands);
        setExecutor("leaveteam", teamCommands);
        setExecutor("emptyteams", teamCommands);

        setExecutor("scatter", new ScatterCommandConversational());
        setExecutor("freeze", new FreezeCommand());
        setExecutor("feature", new FeatureCommand());
        setExecutor("generateborder", new BorderManager());
        setExecutor("givedrops", new GiveDropCommand());
        try {
            setExecutor("timer", new TimerCommand());
        } catch (NoClassDefFoundError ignored) {
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

    /**
     * Load all the default features into the feature manager
     */
    @SuppressWarnings({"OverlyCoupledMethod", "FeatureEnvy"})
    public void loadDefaultFeatures() {
        Logger log = Bukkit.getLogger();
        log.info("Loading UHC feature modules...");
        //Load the default features with settings in config
        FeatureManager featureManager = FeatureManager.getInstance();
        FileConfiguration config = ConfigManager.getInstance().getConfig();
        try {
            featureManager.addFeature(new DeathLightningFeature(), config.getBoolean(ConfigNodes.DEATH_LIGHTNING));
            featureManager.addFeature(new EnderpearlsFeature(), config.getBoolean(ConfigNodes.NO_ENDERPEARL_DAMAGE));
            featureManager.addFeature(new GhastDropsFeature(), config.getBoolean(ConfigNodes.GHAST_DROP_CHANGES));
            featureManager.addFeature(new PlayerHeadsFeature(), config.getBoolean(ConfigNodes.DROP_PLAYER_HEAD));
            featureManager.addFeature(new PlayerListFeature(), config.getBoolean(ConfigNodes.PLAYER_LIST_HEALTH));
            featureManager.addFeature(new RecipeFeature(), config.getBoolean(ConfigNodes.RECIPE_CHANGES));
            featureManager.addFeature(new RegenFeature(), config.getBoolean(ConfigNodes.NO_HEALTH_REGEN));
            featureManager.addFeature(new DeathMessagesFeature(), config.getBoolean(ConfigNodes.DEATH_MESSAGES_ENABLED));
            featureManager.addFeature(new DeathDropsFeature(), config.getBoolean(ConfigNodes.DEATH_DROPS_ENABLED));
            featureManager.addFeature(new AnonChatFeature(), config.getBoolean(ConfigNodes.ANON_CHAT_ENABLED));
            featureManager.addFeature(new GoldenHeadsFeature(), config.getBoolean(ConfigNodes.GOLDEN_HEADS_ENABLED));
            featureManager.addFeature(new DeathBansFeature(), config.getBoolean(ConfigNodes.DEATH_BANS_ENABLED));
            featureManager.addFeature(new PotionNerfsFeature(), config.getBoolean(ConfigNodes.POTION_NERFS_ENABLED));
            featureManager.addFeature(new NetherFeature(), config.getBoolean(ConfigNodes.NETHER_DISABLE_ENABELD));
            featureManager.addFeature(new WitchSpawnsFeature(), config.getBoolean(ConfigNodes.WITCH_SPAWNS_ENABLED));
            featureManager.addFeature(new PortalsFeature(), config.getBoolean(ConfigNodes.PORTAL_RANGES_ENABLED));

            //load the protocollib features last
            featureManager.addFeature(new HardcoreHeartsFeature(), config.getBoolean(ConfigNodes.HARDCORE_HEARTS_ENABLED));
            featureManager.addFeature(new FootprintFeature(), config.getBoolean(ConfigNodes.FOOTPRINTS_ENABLED));
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
    @SuppressWarnings({"OverlyCoupledMethod", "FeatureEnvy"})
    public void loadDefaultConfigurations(){
        ConfigManager configManager = ConfigManager.getInstance();
        configManager.addConfiguration(ConfigType.MAIN, ConfigManager.getFromFile("main.yml", true));
        configManager.addConfiguration(ConfigType.BANS, ConfigManager.getFromFile("bans.yml", true));
    }

    public void loadDefaultScatterTypes(){
        ScatterManager scatterManager = ScatterManager.getInstance();
        try {
            scatterManager.addScatterType(new EvenCircumferenceType());
            scatterManager.addScatterType(new RandomCircularType());
            scatterManager.addScatterType(new RandomSquareType());
        } catch (ScatterTypeConflictException ignored) {
            Bukkit.getLogger().severe("Conflict error when loading default scatter types!");
        }
    }
}