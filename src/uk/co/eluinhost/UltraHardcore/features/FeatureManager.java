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

public class FeatureManager {

	private static UHCFeatureList features = new UHCFeatureList();
	
	private static Pattern name_pattern = Pattern.compile("^[\\w]++$");
	
	public static void addFeature(UHCFeature feature) throws FeatureIDConflictException, InvalidFeatureIDException{
		Matcher mat = name_pattern.matcher(feature.getFeatureID());
		if(!mat.matches()){
			throw new InvalidFeatureIDException();
		}
		for(UHCFeature f : features){
			if(f.getFeatureID().equals(feature.getFeatureID())){
				throw new FeatureIDConflictException();
			}
		}
		features.add(feature);
		if(feature.isEnabled()){
			feature.enableFeature();
		}else{
			feature.disableFeature();
		}
		Bukkit.getPluginManager().registerEvents(feature, UltraHardcore.getInstance());
	}
	
	public static boolean isEnabled(String ID) throws FeatureIDNotFoundException{
		for(UHCFeature feature : features){
			if(feature.getFeatureID().equals(ID)){
				return feature.isEnabled();
			}
		}
		throw new FeatureIDNotFoundException();
	}
	
	public static UHCFeature getFeature(String ID) throws FeatureIDNotFoundException{
		for(UHCFeature feature : features){
			if(feature.getFeatureID().equals(ID)){
				return feature;
			}
		}
		throw new FeatureIDNotFoundException();
	}
	
	public static UHCFeatureList getFeatures(){
		return features;
	}
	
	public static List<String> getFeatureNames(){
		ArrayList<String> f = new ArrayList<String>();
		for(UHCFeature uhc : features){
			f.add(uhc.getFeatureID());
		}
		return f;
	}
}
