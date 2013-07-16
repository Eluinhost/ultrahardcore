package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class MoreFood extends UHCFeature{

	private ItemStack raw_mutton = new ItemStack(Material.RAW_BEEF,1);
	{
		ItemMeta im = raw_mutton.getItemMeta();
		im.setDisplayName("Raw Mutton");
		raw_mutton.setItemMeta(im);
	}
	
	private ItemStack mutton = new ItemStack(Material.COOKED_BEEF,1);
	{
		ItemMeta im = mutton.getItemMeta();
		im.setDisplayName("Mutton");
		mutton.setItemMeta(im);
	}
	
	private ItemStack raw_calamari = new ItemStack(Material.RAW_FISH,1);
	{
		ItemMeta im = raw_calamari.getItemMeta();
		im.setDisplayName("Raw Calamari");
		raw_calamari.setItemMeta(im);
	}
	
	private ItemStack calamari = new ItemStack(Material.COOKED_FISH,1);
	{
		ItemMeta im = calamari.getItemMeta();
		im.setDisplayName("Fried Calamari");
		calamari.setItemMeta(im);
	}
	
	public MoreFood(boolean enabled) {
		super(enabled);
		setFeatureID("MoreFood");
		setDescription("More food drops");
	}

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent ede){
		if(isEnabled()){
			LivingEntity e = ede.getEntity();
			switch(e.getType()){
				case SHEEP:
					ede.getDrops().add(raw_mutton);
					break;
				case SQUID:
					ede.getDrops().add(raw_calamari);
					break;
				default:
			}
		}
	}
		
	@EventHandler
	public void onSmelt(FurnaceSmeltEvent f){
		if(isEnabled()){
			ItemStack is = f.getSource();
			ItemMeta im = is.getItemMeta();
			if(im.hasDisplayName()){
				Furnace furn = (Furnace) f.getBlock().getState();
				FurnaceInventory fi = furn.getInventory();
				ItemStack result = fi.getResult();
				if(im.getDisplayName().equals("Raw Mutton")){
					if(result == null || (result.getItemMeta().hasDisplayName()&&result.getItemMeta().getDisplayName().equals("Mutton"))){
						f.setResult(mutton);
					}else{
						f.setCancelled(true);
						furn.setCookTime((short) (190));
					}
				}else if(im.getDisplayName().equals("Raw Calamari")){
					if(result == null || (result.getItemMeta().hasDisplayName()&&result.getItemMeta().getDisplayName().equals("Fried Calamari"))){
						f.setResult(calamari);
					}else{
						f.setCancelled(true);
						furn.setCookTime((short) (190));
					}
				}
			}
		}
	}
}
