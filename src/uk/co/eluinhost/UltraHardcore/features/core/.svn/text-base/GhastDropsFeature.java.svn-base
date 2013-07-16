package uk.co.eluinhost.UltraHardcore.features.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

/**
 * Handles conversion of ghast tears to gold ingots
 * 
 * Config is whitelist type world dependant
 * Nothing special to do on disable and enable
 * @author Graham
 *
 */
public class GhastDropsFeature extends UHCFeature{
	
	public GhastDropsFeature(boolean enabled) {
		super(enabled);
		setFeatureID("GhastDrops");
		setDescription("Ghasts drop golden ingots instead of tears");
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent ede){
		if(isEnabled() && ede.getEntityType().equals(EntityType.GHAST)){
			if(ConfigHandler.featureEnabledForWorld(ConfigNodes.GHAST_DROP_CHANGES_NODE, ede.getEntity().getWorld().getName())){
				List<ItemStack> drops = ede.getDrops();
				Iterator<ItemStack> i = drops.iterator();
				List<ItemStack> toAdd = new ArrayList<ItemStack>();
				while(i.hasNext()){
					ItemStack is = i.next();
					if(is.getType().equals(Material.GHAST_TEAR)){
						ItemStack newStack = new ItemStack(Material.GOLD_INGOT, is.getAmount());
						i.remove();
						toAdd.add(newStack);
					}
				}
				drops.addAll(toAdd);
			}
		}
	}

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}
}
