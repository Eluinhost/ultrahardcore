package uk.co.eluinhost.ultrahardcore.features.potionnerfs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class PotionNerfsFeature extends UHCFeature {

    public static final String POTION_BASE = BASE_PERMISSION + "potions.";
    public static final String DENY_SPLASH = POTION_BASE + "disableSplash";
    public static final String DENY_IMPROVED = POTION_BASE + "disableImproved";

    private final boolean m_disableSplash;
    private final boolean m_disableAbsorb;
    private final boolean m_disableGlowstone;

    /**
     * Disallows tier 2 + splash when enabled, normal when disabled
     */
    public PotionNerfsFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, "PotionNerfs","Applies nerfs to potions", configManager);
        FileConfiguration config = configManager.getConfig();
        m_disableSplash = config.getBoolean(getBaseConfig()+"disableSplash");
        m_disableAbsorb = config.getBoolean(getBaseConfig()+"disableAbsorb");
        m_disableGlowstone = config.getBoolean(getBaseConfig()+"disableGlowstone");
    }

    /**
     * Runs on a inventory click
     * @param ice InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent ice) {
        //if we're enabled
        if (isEnabled()) {
            //if it's not a brewing stand skip
            if (ice.getInventory().getType() != InventoryType.BREWING) {
                return;
            }

            InventoryView iv = ice.getView();
            boolean cancel = false;

            //if the player is shift clicking
            if (ice.isShiftClick()) {
                //if splash disabled and they're clicking sulphur and don't have permission
                if (m_disableSplash && ice.getCurrentItem().getType() == Material.SULPHUR && ice.getWhoClicked().hasPermission(DENY_SPLASH)) {
                    cancel = true;
                }
                //if tier 2 is disabled and they're clicking glowstone and don't have permission
                if (m_disableGlowstone && ice.getCurrentItem().getType() == Material.GLOWSTONE_DUST && ice.getWhoClicked().hasPermission(DENY_IMPROVED)) {
                    cancel = true;
                }
            }

            //if its the fuel slot that was clicked
            if (ice.getSlotType() == InventoryType.SlotType.FUEL) {
                //if splash disabled and sulphur is on the cursor and no permission
                if (m_disableSplash && iv.getCursor().getType() == Material.SULPHUR && ice.getWhoClicked().hasPermission(DENY_SPLASH)) {
                    cancel = true;
                }
                //if tier 2 disabled and glowstone is on the cursor and no permission
                if (m_disableGlowstone && iv.getCursor().getType() == Material.GLOWSTONE_DUST && ice.getWhoClicked().hasPermission(DENY_IMPROVED)) {
                    cancel = true;
                }
            }

            //if they didn't have permission for action
            if (cancel) {
                //cancel the event
                ice.setCancelled(true);
                //close the inventory
                ice.getWhoClicked().closeInventory();
                //send the player a message saying they can't do it
                ((CommandSender) ice.getWhoClicked()).sendMessage(ChatColor.RED + "You don't have permission to use that ingredient!");
            }
        }
    }

    /**
     * Runs on a player eating
     * @param pee PlayerItemConsumeEvent
     */
    @EventHandler
    public void onPlayerEatEvent(PlayerItemConsumeEvent pee) {
        //if we're enabled and absorbtion is disabled
        if (isEnabled() && m_disableAbsorb) {
            //if they ate a golden apple
            ItemStack is = pee.getItem();
            if (is.getType() == Material.GOLDEN_APPLE) {
                //remove the absorption effect for the player on the next tick
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(),new RemoveAbsoptionRunnable(pee.getPlayer().getName()));
            }
        }
    }

    /**
     * Triggered on an item moved in an inventory
     * @param imie InventoryMoveItemEvent
     */
    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent imie) {
        //if we're enabled
        if (isEnabled()) {
            //if the item is being moved into a brewing stand
            if (imie.getDestination().getType() == InventoryType.BREWING) {
                //cancel sulpher if no permission
                if (imie.getItem().getType() == Material.SULPHUR && m_disableSplash) {
                    imie.setCancelled(true);
                }
                //cancel glowstone if no permission
                if (imie.getItem().getType() == Material.GLOWSTONE_DUST && m_disableGlowstone) {
                    imie.setCancelled(true);
                }
            }
        }
    }

    /**
     * When run removes absorption effect from the player name supplied
     */
    private static class RemoveAbsoptionRunnable implements Runnable{

        private final String m_playerName;

        /**
         * Removes absorption from a player when ran
         * @param playerName the player to run for
         */
        RemoveAbsoptionRunnable(String playerName){
            m_playerName = playerName;
        }

        @Override
        public void run() {
            Player p = Bukkit.getPlayerExact(m_playerName);
            if(p != null){
                p.removePotionEffect(PotionEffectType.ABSORPTION);
            }
        }
    }
}
