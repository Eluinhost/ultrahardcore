package uk.co.eluinhost.ultrahardcore.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.FeatureIDConflictException;
import uk.co.eluinhost.ultrahardcore.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.InvalidFeatureIDException;
import uk.co.eluinhost.ultrahardcore.features.core.*;
import uk.co.eluinhost.ultrahardcore.features.events.UHCFeatureInitEvent;

/**
 * Feature Manager Class
 * TODO make this not a utility class
 */
public class FeatureManager {

    /**
     * Stores a list of all the uhcFeatures loaded on the server
     */
    private final List<UHCFeature> m_uhcFeatureList = new ArrayList<UHCFeature>();

    /**
     * Only allow uhcFeatures with this pattern as an ID
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\w]++$");

    /**
     * Add a UHC feature to the manager
     *
     * @param feature UHCFeature the feature to be added
     * @param enabled Whether the feature should be enabled or not after init
     * @throws FeatureIDConflictException when feature with the same ID already exists
     * @throws InvalidFeatureIDException  when the feature has an invalid ID name
     */
    public void addFeature(UHCFeature feature, boolean enabled) throws FeatureIDConflictException, InvalidFeatureIDException {

        //check for alphanumerics
        Matcher mat = NAME_PATTERN.matcher(feature.getFeatureID());
        if (!mat.matches()) {
            throw new InvalidFeatureIDException();
        }

        //check for existing feature of the same name
        for (UHCFeature uhcFeature : m_uhcFeatureList) {
            if (uhcFeature.getFeatureID().equals(feature.getFeatureID())) {
                throw new FeatureIDConflictException();
            }
        }

        //Make an init event for the feature creation
        UHCFeatureInitEvent initEvent = new UHCFeatureInitEvent(feature);

        //call the event
        Bukkit.getServer().getPluginManager().callEvent(initEvent);

        //if it was cancelled return
        if (!initEvent.isAllowed()) {
            return;
        }

        //add the feature

        //TODO change this >.>
        m_uhcFeatureList.add(feature);
        if (feature.isEnabled()) {
            feature.enableFeature();
        } else {
            feature.disableFeature();
        }

        //Register the feature for plugin events
        Bukkit.getPluginManager().registerEvents(feature, UltraHardcore.getInstance());
    }

    /**
     * Check if a feature is enabled by it's ID
     *
     * @param featureID String the ID to check for
     * @return boolean true if enabled, false otherwise
     * @throws FeatureIDNotFoundException when feature not found
     */
    public boolean isFeatureEnabled(String featureID) throws FeatureIDNotFoundException {
        for (UHCFeature feature : m_uhcFeatureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature.isEnabled();
            }
        }
        throw new FeatureIDNotFoundException();
    }

    /**
     * Get the UHCFeature based on it's ID
     *
     * @param featureID String the ID to check for
     * @return UHCFeature the returned feature
     * @throws FeatureIDNotFoundException when feature ID not found
     */
    public UHCFeature getFeatureByID(String featureID) throws FeatureIDNotFoundException {
        for (UHCFeature feature : m_uhcFeatureList) {
            if (feature.getFeatureID().equals(featureID)) {
                return feature;
            }
        }
        throw new FeatureIDNotFoundException();
    }

    /**
     * Returns an unmodifiable list of all of the uhcFeatures loaded
     *
     * @return List
     */
    public List<UHCFeature> getFeatures() {
        return Collections.unmodifiableList(m_uhcFeatureList);
    }

    /**
     * Get a list of all the used feature names
     *
     * @return List String
     */
    public List<String> getFeatureNames() {
        List<String> features = new ArrayList<String>();
        for (UHCFeature uhc : m_uhcFeatureList) {
            features.add(uhc.getFeatureID());
        }
        return features;
    }

    public void loadDefaultModules() {
        Logger log = Bukkit.getLogger();
        log.info("Loading UHC feature modules...");
        //Load the default features with settings in config
        FileConfiguration config = ConfigHandler.getConfig(ConfigHandler.MAIN);
        ArrayList<UHCFeature> features = new ArrayList<UHCFeature>();
        features.add(new DeathLightningFeature(config.getBoolean(ConfigNodes.DEATH_LIGHTNING)));
        features.add(new EnderpearlsFeature(config.getBoolean(ConfigNodes.NO_ENDERPEARL_DAMAGE)));
        features.add(new GhastDropsFeature(config.getBoolean(ConfigNodes.GHAST_DROP_CHANGES)));
        features.add(new PlayerHeadsFeature(config.getBoolean(ConfigNodes.DROP_PLAYER_HEAD)));
        features.add(new PlayerListFeature(config.getBoolean(ConfigNodes.PLAYER_LIST_HEALTH)));
        features.add(new RecipeFeature(config.getBoolean(ConfigNodes.RECIPE_CHANGES)));
        features.add(new RegenFeature(config.getBoolean(ConfigNodes.NO_HEALTH_REGEN)));
        features.add(new DeathMessagesFeature(config.getBoolean(ConfigNodes.DEATH_MESSAGES_ENABLED)));
        features.add(new DeathDropsFeature(config.getBoolean(ConfigNodes.DEATH_DROPS_ENABLED)));
        features.add(new AnonChatFeature(config.getBoolean(ConfigNodes.ANON_CHAT_ENABLED)));
        features.add(new GoldenHeadsFeature(config.getBoolean(ConfigNodes.GOLDEN_HEADS_ENABLED)));
        features.add(new DeathBansFeature(config.getBoolean(ConfigNodes.DEATH_BANS_ENABLED)));
        features.add(new PotionNerfsFeature(config.getBoolean(ConfigNodes.POTION_NERFS_ENABLED)));
        features.add(new NetherFeature(config.getBoolean(ConfigNodes.NETHER_DISABLE_ENABELD)));
        features.add(new WitchSpawnsFeature(config.getBoolean(ConfigNodes.WITCH_SPAWNS_ENABLED)));
        features.add(new PortalsFeature(config.getBoolean(ConfigNodes.PORTAL_RANGES_ENABLED)));
        try {
            features.add(new HardcoreHeartsFeature(config.getBoolean(ConfigNodes.HARDCORE_HEARTS_ENABLED)));
        } catch (NoClassDefFoundError e) {
            log.severe("Cannot find a class for HardcoreHearts, ProtocolLib is needed for this feature to work, disabling...");
        }
        try {
            features.add(new FootprintFeature(config.getBoolean(ConfigNodes.FOOTPRINTS_ENABLED)));
        } catch (NoClassDefFoundError e) {
            log.severe("Cannot find a class for Footprints, ProtocolLib is needed for this feature to work, disabling...");
        }

        for (UHCFeature f : features) {
            try {
                addFeature(f, true);//TODO bool
                log.info("Loaded feature module: " + f.getFeatureID());
            } catch (Exception e) {
                log.severe("Failed to load a module " + (f == null ? "null" : f.getFeatureID()));
                e.printStackTrace();
            }
        }
    }
}
