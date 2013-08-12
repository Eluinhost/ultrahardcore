package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class PotionNerfs extends UHCFeature {

	public PotionNerfs(boolean enabled) {
		super(enabled);
		setFeatureID("PotionNerfs");
		setDescription("Applies nerfs to potions");
	}

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent ice){
        if(isEnabled() && ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.RECIPE_CHANGES_SPLASH)){
            if(ice.getInventory().getType().equals(InventoryType.BREWING)){
                if(!ice.getWhoClicked().hasPermission(PermissionNodes.DENY_SPLASH)){
                    return;
                }
                InventoryView iv = ice.getView();
                boolean cancel = false;
                if(ice.isShiftClick()){
                    if(ice.getCurrentItem().getType().equals(Material.SULPHUR)){
                        cancel = true;
                    }
                }else if(ice.getSlotType().equals(InventoryType.SlotType.FUEL)){
                    if(iv.getCursor().getType().equals(Material.SULPHUR)){
                        cancel = true;
                    }
                }
                if(cancel){
                    ice.setCancelled(true);
                    ice.getWhoClicked().closeInventory();
                    ((Player)ice.getWhoClicked()).sendMessage(ChatColor.RED+"You don't have permission to brew splash potions!");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerEatEvent(PlayerItemConsumeEvent pee){
        if(isEnabled() && ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.DISABLE_ABSORB)){
            ItemStack is = pee.getItem();
            if(is.getType().equals(Material.GOLDEN_APPLE)){
                final String playerName = pee.getPlayer().getName();
                Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(),
                        new Runnable(){
                            @Override
                            public void run() {
                                Player p = Bukkit.getPlayerExact(playerName);
                                p.removePotionEffect(PotionEffectType.ABSORPTION);
                            }
                        }
                );
            }
        }
    }

    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent imie){
        if(isEnabled() && ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.RECIPE_CHANGES_SPLASH)){
            if(imie.getDestination().getType().equals(InventoryType.BREWING)){
                if(imie.getItem().getType().equals(Material.SULPHUR)){
                    imie.setCancelled(true);
                }
            }
        }
    }

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}	
}
