package uk.co.eluinhost.UltraHardcore.features.core;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;


/**
 * PlayerHeadsFeature
 * 
 * Handles the dropping of a player head on death
 * @author ghowden
 *
 */
public class PlayerHeadsFeature extends UHCFeature{
	
	public PlayerHeadsFeature(boolean enabled) {
		super(enabled);
		setFeatureID("PlayerHeads");
		setDescription("Players can drop their heads on death");
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent pde){
		if(isEnabled()){
			if(pde.getEntity().hasPermission(PermissionNodes.DROP_SKULL)){
				if(ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.PLAYER_HEAD_PVP_ONLY)){
					/*if(!(pde.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)){
						return;
					}
					EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) pde.getEntity().getLastDamageCause();
					if(!(edbee.getDamager() instanceof Player)){
						if(edbee.getDamager() instanceof Projectile){
							LivingEntity p = ((Projectile)edbee.getDamager()).getShooter();
							if(!(p instanceof Player)){
								return;
							}
						}else{
							return;
						}
					}*/
					if(pde.getEntity().getKiller() == null){
						return;
					}
				}
				Random r = new Random();
				if(r.nextInt(100)>=(100-ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.PLAYER_HEAD_DROP_CHANCE))){
					pde.getDrops().add(playerSkullForName(pde.getEntity().getName()));
				}
			}
		}
	}	
	
	
	private ItemStack playerSkullForName(String name){
		ItemStack is = new ItemStack(Material.SKULL_ITEM,1);
		is.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		meta.setOwner(name);
		is.setItemMeta(meta);
		return is;
	}

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}
}
