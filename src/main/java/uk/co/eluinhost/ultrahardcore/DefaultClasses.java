package uk.co.eluinhost.ultrahardcore;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.commands.CommandHandler;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.features.exceptions.FeatureIDConflictException;
import uk.co.eluinhost.features.exceptions.InvalidFeatureIDException;
import uk.co.eluinhost.ultrahardcore.borders.RealBorderTypeManager;
import uk.co.eluinhost.ultrahardcore.borders.exceptions.BorderIDConflictException;
import uk.co.eluinhost.ultrahardcore.borders.types.CylinderBorder;
import uk.co.eluinhost.ultrahardcore.borders.types.RoofBorder;
import uk.co.eluinhost.ultrahardcore.borders.types.SquareBorder;
import uk.co.eluinhost.ultrahardcore.commands.*;
import uk.co.eluinhost.ultrahardcore.commands.scatter.ScatterCommand;
import uk.co.eluinhost.ultrahardcore.features.anonchat.AnonChatFeature;
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
import uk.co.eluinhost.ultrahardcore.features.playerfreeze.PlayerFreezeFeature;
import uk.co.eluinhost.ultrahardcore.features.playerheads.PlayerHeadsFeature;
import uk.co.eluinhost.ultrahardcore.features.playerlist.PlayerListFeature;
import uk.co.eluinhost.ultrahardcore.features.portals.PortalsFeature;
import uk.co.eluinhost.ultrahardcore.features.potionnerfs.PotionNerfsFeature;
import uk.co.eluinhost.ultrahardcore.features.recipes.RecipeFeature;
import uk.co.eluinhost.ultrahardcore.features.regen.RegenFeature;
import uk.co.eluinhost.ultrahardcore.features.witchspawns.WitchSpawnsFeature;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.exceptions.ScatterTypeConflictException;
import uk.co.eluinhost.ultrahardcore.scatter.types.EvenCircumferenceType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomCircularType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomSquareType;

import java.util.logging.Logger;

@SuppressWarnings("OverlyCoupledClass")
public class DefaultClasses {

    private final Plugin m_uhc;
    private final ConfigManager m_configManager;
    private final FeatureManager m_featureManager;
    private final RealBorderTypeManager m_borderTypes;
    private final CommandHandler m_commandHandler;
    private final ScatterManager m_scatterManager;
    private final Logger m_logger;

    /**
     * @param plugin
     * @param conifgManager
     * @param featureManager
     * @param borders
     * @param commandHandler
     * @param scatterManager
     * @param logger
     */
    @Inject
    public DefaultClasses(Plugin plugin, ConfigManager conifgManager, FeatureManager featureManager,
                          RealBorderTypeManager borders, CommandHandler commandHandler, ScatterManager scatterManager,
                          Logger logger){
        m_uhc = plugin;
        m_logger = logger;
        m_configManager = conifgManager;
        m_featureManager = featureManager;
        m_borderTypes = borders;
        m_commandHandler = commandHandler;
        m_scatterManager = scatterManager;
    }

    /**
     * Load the default border types
     */
    public void loadDefaultBorders() {
        try{
            m_borderTypes.addBorder(new CylinderBorder());
            m_borderTypes.addBorder(new RoofBorder());
            m_borderTypes.addBorder(new SquareBorder());
        } catch (BorderIDConflictException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load all the default commands
     */
    public void loadDefaultCommands() {
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
                m_commandHandler.registerCommands(clazz);
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
        m_logger.info("Loading UHC feature modules...");
        //Load the default features with settings in config
        try {
            m_featureManager.addFeature(new DeathLightningFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new EnderpearlsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new GhastDropsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new PlayerHeadsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new PlayerListFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new RecipeFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new RegenFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new DeathMessagesFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new DeathDropsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new AnonChatFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new GoldenHeadsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new DeathBansFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new PotionNerfsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new NetherFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new WitchSpawnsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new PortalsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new PlayerFreezeFeature(m_uhc,m_configManager));

            //load the protocollib features last
            m_featureManager.addFeature(new HardcoreHeartsFeature(m_uhc,m_configManager));
            m_featureManager.addFeature(new FootprintFeature(m_uhc,m_configManager));
        } catch (FeatureIDConflictException ignored) {
            m_logger.severe("A default UHC Feature ID is conflicting, this should never happen!");
        } catch (InvalidFeatureIDException ignored) {
            m_logger.severe("A default UHC feature ID is invalid, this should never happen!");
        } catch (NoClassDefFoundError ignored) {
            m_logger.severe("Couldn't find protocollib for related features, skipping them all.");
        }
    }

    /**
     * Add the default config files
     */
    public void loadDefaultConfigurations(){
        m_configManager.addConfiguration("main", m_configManager.getFromFile("main.yml", true));
        m_configManager.addConfiguration("bans", m_configManager.getFromFile("bans.yml", true));
        m_configManager.addConfiguration("words", m_configManager.getFromFile("words.yml", true));
    }

    /**
     * Load the default scatter types
     */
    public void loadDefaultScatterTypes(){
        try {
            m_scatterManager.addScatterType(new EvenCircumferenceType());
            m_scatterManager.addScatterType(new RandomCircularType());
            m_scatterManager.addScatterType(new RandomSquareType());
        } catch (ScatterTypeConflictException ignored) {
            Bukkit.getLogger().severe("Conflict error when loading default scatter types!");
        }
    }
}
