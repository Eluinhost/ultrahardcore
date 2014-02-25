package uk.co.eluinhost.ultrahardcore.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.features.FeatureException;
import uk.co.eluinhost.ultrahardcore.exceptions.features.FeatureIDConflictException;
import uk.co.eluinhost.ultrahardcore.exceptions.features.FeatureIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.features.InvalidFeatureIDException;
import uk.co.eluinhost.ultrahardcore.features.*;
import uk.co.eluinhost.ultrahardcore.events.features.UHCFeatureInitEvent;
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
import uk.co.eluinhost.ultrahardcore.features.playerheads.PlayerHeadsFeature;
import uk.co.eluinhost.ultrahardcore.features.playerlist.PlayerListFeature;
import uk.co.eluinhost.ultrahardcore.features.portals.PortalsFeature;
import uk.co.eluinhost.ultrahardcore.features.potionnerfs.PotionNerfsFeature;
import uk.co.eluinhost.ultrahardcore.features.recipes.RecipeFeature;
import uk.co.eluinhost.ultrahardcore.features.regen.RegenFeature;
import uk.co.eluinhost.ultrahardcore.features.witchspawns.WitchSpawnsFeature;

/**
 * Feature Manager Class
 */
public class FeatureManager {

    /**
     * Stores a list of all the uhcFeatures loaded on the server
     */
    private final List<IUHCFeature> m_uhcFeatureList = new ArrayList<IUHCFeature>();

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
        String featureID = feature.getFeatureID();

        //check for right pattern
        Matcher mat = NAME_PATTERN.matcher(featureID);
        if (!mat.matches()) {
            throw new InvalidFeatureIDException();
        }

        //check for existing feature of the same name
        for (IUHCFeature uhcFeature : m_uhcFeatureList) {
            if (uhcFeature.equals(feature)) {
                throw new FeatureIDConflictException();
            }
        }

        //Make an init event for the feature creation
        UHCFeatureInitEvent initEvent = new UHCFeatureInitEvent(feature);

        //call the event
        Bukkit.getPluginManager().callEvent(initEvent);

        //if it was cancelled return
        if (initEvent.isCancelled()) {
            Bukkit.getLogger().log(Level.SEVERE,"Init event cancelled for feature "+featureID);
            return;
        }

        //add the feature
        m_uhcFeatureList.add(feature);
        Bukkit.getLogger().log(Level.INFO,"Loaded feature module "+featureID);

        if(enabled){
            feature.enableFeature();
        }else{
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
        for (IUHCFeature feature : m_uhcFeatureList) {
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
    public IUHCFeature getFeatureByID(String featureID) throws FeatureIDNotFoundException {
        for (IUHCFeature feature : m_uhcFeatureList) {
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
    public List<IUHCFeature> getFeatures() {
        return Collections.unmodifiableList(m_uhcFeatureList);
    }

    /**
     * Get a list of all the used feature names
     *
     * @return List String
     */
    public List<String> getFeatureNames() {
        List<String> features = new ArrayList<String>();
        for (IUHCFeature uhc : m_uhcFeatureList) {
            features.add(uhc.getFeatureID());
        }
        return features;
    }

    public void loadDefaultModules() {
        Logger log = Bukkit.getLogger();
        log.info("Loading UHC feature modules...");
        //Load the default features with settings in config
        FileConfiguration config = ConfigManager.getConfig(ConfigManager.MAIN);
        try {
            addFeature(new DeathLightningFeature(), config.getBoolean(ConfigNodes.DEATH_LIGHTNING));
            addFeature(new EnderpearlsFeature(), config.getBoolean(ConfigNodes.NO_ENDERPEARL_DAMAGE));
            addFeature(new GhastDropsFeature(), config.getBoolean(ConfigNodes.GHAST_DROP_CHANGES));
            addFeature(new PlayerHeadsFeature(), config.getBoolean(ConfigNodes.DROP_PLAYER_HEAD));
            addFeature(new PlayerListFeature(), config.getBoolean(ConfigNodes.PLAYER_LIST_HEALTH));
            addFeature(new RecipeFeature(), config.getBoolean(ConfigNodes.RECIPE_CHANGES));
            addFeature(new RegenFeature(), config.getBoolean(ConfigNodes.NO_HEALTH_REGEN));
            addFeature(new DeathMessagesFeature(), config.getBoolean(ConfigNodes.DEATH_MESSAGES_ENABLED));
            addFeature(new DeathDropsFeature(), config.getBoolean(ConfigNodes.DEATH_DROPS_ENABLED));
            addFeature(new AnonChatFeature(), config.getBoolean(ConfigNodes.ANON_CHAT_ENABLED));
            addFeature(new GoldenHeadsFeature(), config.getBoolean(ConfigNodes.GOLDEN_HEADS_ENABLED));
            addFeature(new DeathBansFeature(), config.getBoolean(ConfigNodes.DEATH_BANS_ENABLED));
            addFeature(new PotionNerfsFeature(), config.getBoolean(ConfigNodes.POTION_NERFS_ENABLED));
            addFeature(new NetherFeature(), config.getBoolean(ConfigNodes.NETHER_DISABLE_ENABELD));
            addFeature(new WitchSpawnsFeature(), config.getBoolean(ConfigNodes.WITCH_SPAWNS_ENABLED));
            addFeature(new PortalsFeature(), config.getBoolean(ConfigNodes.PORTAL_RANGES_ENABLED));

            //load the protocollib features last
            addFeature(new HardcoreHeartsFeature(), config.getBoolean(ConfigNodes.HARDCORE_HEARTS_ENABLED));
            addFeature(new FootprintFeature(), config.getBoolean(ConfigNodes.FOOTPRINTS_ENABLED));
        } catch (FeatureException ignored) {
            log.severe("Default UHC Features are conflicting, this should never happen!");
        } catch (NoClassDefFoundError ignored) {
            log.severe("Couldn't find protocollib for related features, not loading them.");
        }
    }
}
