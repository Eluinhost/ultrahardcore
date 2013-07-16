package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;


/**
 * DeathLightningFeature
 * 
 * hits people with lightning on death
 * @author ghowden
 *
 */
public class DeathLightningFeature extends UHCFeature{
	
	public DeathLightningFeature(boolean enabled) {
		super(enabled);
		setFeatureID("DeathLightning");
		setDescription("Fake lightning on a player's corpse");
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent pde){
		if(isEnabled()){
			if(pde.getEntity().hasPermission(PermissionNodes.DEATH_LIGHTNING)){
				pde.getEntity().getWorld().strikeLightningEffect(pde.getEntity().getLocation());
			}
		}
	}	
	
	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}
}
