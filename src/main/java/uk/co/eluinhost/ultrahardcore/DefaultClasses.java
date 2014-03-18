package uk.co.eluinhost.ultrahardcore;

import com.google.inject.Inject;
import com.google.inject.Injector;
import uk.co.eluinhost.commands.CommandHandler;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.features.IFeature;
import uk.co.eluinhost.features.exceptions.FeatureIDConflictException;
import uk.co.eluinhost.features.exceptions.InvalidFeatureIDException;
import uk.co.eluinhost.ultrahardcore.borders.BorderTypeManager;
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
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;
import uk.co.eluinhost.ultrahardcore.scatter.types.EvenCircumferenceType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomCircularType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomSquareType;

import java.util.logging.Logger;

@SuppressWarnings("OverlyCoupledClass")
public class DefaultClasses {

    private final ConfigManager m_configManager;
    private final FeatureManager m_featureManager;
    private final BorderTypeManager m_borderTypes;
    private final CommandHandler m_commandHandler;
    private final ScatterManager m_scatterManager;
    private final Logger m_logger;

    /**
     * @param conifgManager the config manager
     * @param featureManager the feature manager
     * @param borders the border manager
     * @param commandHandler the command handler
     * @param scatterManager the scatter manager
     * @param logger the logger
     */
    @Inject
    public DefaultClasses(ConfigManager conifgManager, FeatureManager featureManager,
                          BorderTypeManager borders, CommandHandler commandHandler, ScatterManager scatterManager,
                          Logger logger){
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
     * @param injector the injector
     */
    @SuppressWarnings("OverlyCoupledMethod")
    public void loadDefaultFeatures(Injector injector) {
        m_logger.info("Loading UHC feature modules...");
        //Load the default features with settings in config
        Class<? extends IFeature>[] classes = new Class[]{
                DeathLightningFeature.class,
                EnderpearlsFeature.class,
                GhastDropsFeature.class,
                PlayerHeadsFeature.class,
                PlayerListFeature.class,
                RecipeFeature.class,
                RegenFeature.class,
                DeathMessagesFeature.class,
                DeathDropsFeature.class,
                AnonChatFeature.class,
                GoldenHeadsFeature.class,
                DeathBansFeature.class,
                PotionNerfsFeature.class,
                NetherFeature.class,
                WitchSpawnsFeature.class,
                PortalsFeature.class,
                PlayerFreezeFeature.class,
                HardcoreHeartsFeature.class,
                FootprintFeature.class
        };
        for(Class<? extends IFeature> clazz : classes){
            try{
                IFeature feature = injector.getInstance(clazz);
                m_featureManager.addFeature(feature);
            } catch (FeatureIDConflictException ignored) {
                m_logger.severe("A default UHC Feature ID is conflicting, this should never happen!");
            } catch (InvalidFeatureIDException ignored) {
                m_logger.severe("A default UHC feature ID is invalid, this should never happen!");
            } catch (NoClassDefFoundError ignored) {
                m_logger.severe("Couldn't find protocollib for related feature, skipping...");
            }
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
     * @param injector the injector
     */
    public void loadDefaultScatterTypes(Injector injector){
        Class<? extends AbstractScatterType>[] types = new Class[]{
                EvenCircumferenceType.class,
                RandomCircularType.class,
                RandomSquareType.class
        };
        for(Class<? extends AbstractScatterType> clazz : types){
            try {
                AbstractScatterType type = injector.getInstance(clazz);
                m_scatterManager.addScatterType(type);
            } catch (ScatterTypeConflictException ignored) {
                m_logger.severe("Conflict error when loading default scatter types!");
            }
        }
    }
}
