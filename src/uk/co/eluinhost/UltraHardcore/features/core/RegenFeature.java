package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;


/**
 * RegenHandler
 * 
 * Handles the regeneration of players and cancels if its from being near full hunger
 * @author ghowden
 *
 */
public class RegenFeature extends UHCFeature{
	
	public RegenFeature(boolean enabled) {
		super(enabled);
		setFeatureID("DisableRegen");
		setDescription("Cancels a player's passive health regeneration");
	}

	@EventHandler
	public void onHealthRegen(EntityRegainHealthEvent erhe){
		if(isEnabled()){
			//If its a player regen
			if(erhe.getEntityType().equals(EntityType.PLAYER)){
				//If the player is in a hardcore world
				//If its just standard health regen
				if(erhe.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)){
					if(((Player)erhe.getEntity()).hasPermission(PermissionNodes.NO_HEALTH_REGEN)){
						//Cancel the event to stop the regen
						erhe.setCancelled(true);
					}
				}					
			}
		}
	}

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}
}
