package com.publicuhc.ultrahardcore;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.publicuhc.commands.CommandHandler;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.features.FeatureManager;
import com.publicuhc.features.IFeature;
import com.publicuhc.features.exceptions.FeatureIDConflictException;
import com.publicuhc.features.exceptions.InvalidFeatureIDException;
import com.publicuhc.ultrahardcore.borders.BorderTypeManager;
import com.publicuhc.ultrahardcore.borders.exceptions.BorderIDConflictException;
import com.publicuhc.ultrahardcore.borders.types.CylinderBorder;
import com.publicuhc.ultrahardcore.borders.types.RoofBorder;
import com.publicuhc.ultrahardcore.borders.types.SquareBorder;
import com.publicuhc.ultrahardcore.commands.*;
import com.publicuhc.ultrahardcore.commands.scatter.ScatterCommand;
import com.publicuhc.ultrahardcore.features.anonchat.AnonChatFeature;
import com.publicuhc.ultrahardcore.features.deathbans.DeathBansFeature;
import com.publicuhc.ultrahardcore.features.deathdrops.DeathDropsFeature;
import com.publicuhc.ultrahardcore.features.deathlightning.DeathLightningFeature;
import com.publicuhc.ultrahardcore.features.deathmessages.DeathMessagesFeature;
import com.publicuhc.ultrahardcore.features.enderpearls.EnderpearlsFeature;
import com.publicuhc.ultrahardcore.features.footprints.FootprintFeature;
import com.publicuhc.ultrahardcore.features.ghastdrops.GhastDropsFeature;
import com.publicuhc.ultrahardcore.features.goldenheads.GoldenHeadsFeature;
import com.publicuhc.ultrahardcore.features.hardcorehearts.HardcoreHeartsFeature;
import com.publicuhc.ultrahardcore.features.nether.NetherFeature;
import com.publicuhc.ultrahardcore.features.playerfreeze.PlayerFreezeFeature;
import com.publicuhc.ultrahardcore.features.playerheads.PlayerHeadsFeature;
import com.publicuhc.ultrahardcore.features.playerlist.PlayerListFeature;
import com.publicuhc.ultrahardcore.features.portals.PortalsFeature;
import com.publicuhc.ultrahardcore.features.potionnerfs.PotionNerfsFeature;
import com.publicuhc.ultrahardcore.features.recipes.RecipeFeature;
import com.publicuhc.ultrahardcore.features.regen.RegenFeature;
import com.publicuhc.ultrahardcore.features.timer.TimerFeature;
import com.publicuhc.ultrahardcore.features.witchspawns.WitchSpawnsFeature;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import com.publicuhc.ultrahardcore.scatter.exceptions.ScatterTypeConflictException;
import com.publicuhc.ultrahardcore.scatter.types.AbstractScatterType;
import com.publicuhc.ultrahardcore.scatter.types.EvenCircumferenceType;
import com.publicuhc.ultrahardcore.scatter.types.RandomCircularType;
import com.publicuhc.ultrahardcore.scatter.types.RandomSquareType;

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
    @SuppressWarnings("OverlyCoupledMethod")
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
                ScatterCommand.class,
                TimerCommand.class
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
                FootprintFeature.class,
                TimerFeature.class
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
