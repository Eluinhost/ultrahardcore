package uk.co.eluinhost.UltraHardcore.features;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.exceptions.FeatureIDConflictException;
import uk.co.eluinhost.UltraHardcore.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.UltraHardcore.exceptions.InvalidFeatureIDException;
import uk.co.eluinhost.UltraHardcore.features.events.UHCFeatureInitEvent;

public class FeatureManager {

    /**
     * Stores a list of all the features loaded on the server
     */
	private static ArrayList<UHCFeature> features = new ArrayList<UHCFeature>();

    /**
     * Only allow features with this pattern as an ID
     */
	private final static Pattern name_pattern = Pattern.compile("^[\\w]++$");

    /**
     * Add a UHC feature to the manager
     * @param feature UHCFeature the feature to be added
     * @param enabled Whether the feature should be enabled or not after init
     * @throws FeatureIDConflictException when feature with the same ID already exists
     * @throws InvalidFeatureIDException when the feature has an invalid ID name
     */
	public static void addFeature(UHCFeature feature,boolean enabled) throws FeatureIDConflictException, InvalidFeatureIDException{

        //check for alphanumerics
		Matcher mat = name_pattern.matcher(feature.getFeatureID());
		if(!mat.matches()){
			throw new InvalidFeatureIDException();
		}

        //check for existing feature of the same name
		for(UHCFeature f : features){
			if(f.getFeatureID().equals(feature.getFeatureID())){
				throw new FeatureIDConflictException();
			}
		}

        //Make an init event for the feature creation
        UHCFeatureInitEvent uhc_init_event = new UHCFeatureInitEvent(feature);

        //call the event
        Bukkit.getServer().getPluginManager().callEvent(uhc_init_event);

        //if it was cancelled return
        if(!uhc_init_event.isAllowed()){
            return;
        }

        //add the feature

        //TODO change this >.>
		features.add(feature);
		if(feature.isEnabled()){
			feature.enableFeature();
		}else{
			feature.disableFeature();
		}

        //Register the feature for plugin events
		Bukkit.getPluginManager().registerEvents(feature, UltraHardcore.getInstance());
	}

    /**
     * Check if a feature is enabled by it's ID
     * @param ID String the ID to check for
     * @return boolean true if enabled, false otherwise
     * @throws FeatureIDNotFoundException when feature not found
     */
	public static boolean isEnabled(String ID) throws FeatureIDNotFoundException{
		for(UHCFeature feature : features){
			if(feature.getFeatureID().equals(ID)){
				return feature.isEnabled();
			}
		}
		throw new FeatureIDNotFoundException();
	}

    /**
     * Get the UHCFeature based on it's ID
     * @param ID String the ID to check for
     * @return UHCFeature the returned feature
     * @throws FeatureIDNotFoundException when feature ID not found
     */
	public static UHCFeature getFeature(String ID) throws FeatureIDNotFoundException{
		for(UHCFeature feature : features){
			if(feature.getFeatureID().equals(ID)){
				return feature;
			}
		}
		throw new FeatureIDNotFoundException();
	}

    /**
     * Returns all of the features loaded
     * @return ArrayList
     */
	public static ArrayList<UHCFeature> getFeatures(){
		return features;
	}

    /**
     * Get a list of all the used feature names
     * @return List String
     */
	public static List<String> getFeatureNames(){
		ArrayList<String> f = new ArrayList<String>();
		for(UHCFeature uhc : features){
			f.add(uhc.getFeatureID());
		}
		return f;
	}
}
