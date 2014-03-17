package uk.co.eluinhost.ultrahardcore;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.commands.CommandHandler;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.ultrahardcore.borders.BorderTypeManager;
import uk.co.eluinhost.ultrahardcore.borders.exceptions.BorderIDConflictException;
import uk.co.eluinhost.ultrahardcore.borders.types.CylinderBorder;
import uk.co.eluinhost.ultrahardcore.borders.types.RoofBorder;
import uk.co.eluinhost.ultrahardcore.borders.types.SquareBorder;
import uk.co.eluinhost.ultrahardcore.commands.*;
import uk.co.eluinhost.ultrahardcore.commands.scatter.ScatterCommand;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.exceptions.ScatterTypeConflictException;
import uk.co.eluinhost.ultrahardcore.scatter.types.EvenCircumferenceType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomCircularType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomSquareType;

public class DefaultClasses {

    private final Plugin m_uhc;
    private final ConfigManager m_configManager;
    private final FeatureManager m_featureManager;
    private final BorderTypeManager m_borderTypes;
    private final CommandHandler m_commandHandler;
    private final ScatterManager m_scatterManager;

    /**
     * @param plugin
     * @param conifgManager
     * @param featureManager
     * @param borders
     * @param commandHandler
     * @param scatterManager
     */
    @Inject
    public DefaultClasses(Plugin plugin, ConfigManager conifgManager, FeatureManager featureManager,
                          BorderTypeManager borders, CommandHandler commandHandler, ScatterManager scatterManager){
        m_uhc = plugin;
        m_configManager = conifgManager;
        m_featureManager = featureManager;
        m_borderTypes = borders;
        m_commandHandler = commandHandler;
        m_scatterManager = scatterManager;
    }

    /**
     * Load the default border types
     */
    private void loadDefaultBorders() {
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
    private void loadDefaultCommands() {
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
    public void loadDefaultFeatures() {
        /**TODO need to inject something somehow here
        Logger log = Bukkit.getLogger();
        log.info("Loading UHC feature modules...");
        //Load the default features with settings in config
        try {
            m_featureManager.addFeature(new DeathLightningFeature(this,m_configManager));
            m_featureManager.addFeature(new EnderpearlsFeature(this,m_configManager));
            m_featureManager.addFeature(new GhastDropsFeature(this,m_configManager));
            m_featureManager.addFeature(new PlayerHeadsFeature(this,m_configManager));
            m_featureManager.addFeature(new PlayerListFeature(this,m_configManager));
            m_featureManager.addFeature(new RecipeFeature(this,m_configManager));
            m_featureManager.addFeature(new RegenFeature(this,m_configManager));
            m_featureManager.addFeature(new DeathMessagesFeature(this,m_configManager));
            m_featureManager.addFeature(new DeathDropsFeature(this,m_configManager));
            m_featureManager.addFeature(new AnonChatFeature(this,m_configManager));
            m_featureManager.addFeature(new GoldenHeadsFeature(this,m_configManager));
            m_featureManager.addFeature(new DeathBansFeature(this,m_configManager));
            m_featureManager.addFeature(new PotionNerfsFeature(this,m_configManager));
            m_featureManager.addFeature(new NetherFeature(this,m_configManager));
            m_featureManager.addFeature(new WitchSpawnsFeature(this,m_configManager));
            m_featureManager.addFeature(new PortalsFeature(this,m_configManager));
            m_featureManager.addFeature(new PlayerFreezeFeature(this,m_configManager));

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
         */
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
            scatterManager.addScatterType(new RandomCircularType());
            scatterManager.addScatterType(new RandomSquareType());
        } catch (ScatterTypeConflictException ignored) {
            Bukkit.getLogger().severe("Conflict error when loading default scatter types!");
        }
    }
}
