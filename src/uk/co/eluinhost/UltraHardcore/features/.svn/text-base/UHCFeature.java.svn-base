package uk.co.eluinhost.UltraHardcore.features;

import org.bukkit.event.Listener;

import uk.co.eluinhost.UltraHardcore.exceptions.FeatureStateNotChangedException;

public abstract class UHCFeature implements Listener{

	protected String featureID = "INVALID";
	private boolean enabled = false;
	private String description = "N/A";
		
	public abstract void enableFeature();
	public abstract void disableFeature();

	protected final void setFeatureID(String ID){
		featureID = ID;
	}
	public final String getFeatureID() {
		return featureID;
	}

	public final boolean isEnabled() {
		return enabled;
	}
	
	public final void setEnabled(boolean enable) throws FeatureStateNotChangedException {
		//if we're changing state
		if(enable != isEnabled()){
			if(enable){
				enabled = true;
				enableFeature();
			}else{
				enabled = false;
				disableFeature();
			}
		}else{
			throw new FeatureStateNotChangedException();
		}
	}

    public UHCFeature(boolean enabled){
		this.enabled = enabled;
	}
	
	@Override
	public boolean equals(Object o){
		return (o instanceof UHCFeature && ((UHCFeature)o).getFeatureID().equals(this.getFeatureID()));
	}
	public final String getDescription() {
		return description;
	}
	public final void setDescription(String description) {
		this.description = description;
	}
}
