package uk.co.eluinhost.UltraHardcore.features.core;

import java.util.ArrayList;
import java.util.Collection;
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
 * Config is whitelist type world dependant
 * Nothing special to do on disable and enable
 *
 * @author Graham
 */
public class GhastDropsFeature extends UHCFeature {

    public GhastDropsFeature(boolean enabled) {
        super("GhastDrops", enabled);
        setDescription("Ghasts drop golden ingots instead of tears");
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent ede) {
        //if we're enabled and a ghast died
        if (isEnabled() && ede.getEntityType() == EntityType.GHAST) {
            //if ghasts can't drop tears in this world
            if (ConfigHandler.featureEnabledForWorld(ConfigNodes.GHAST_DROP_CHANGES_NODE, ede.getEntity().getWorld().getName())) {

                //get the list of drops
                List<ItemStack> drops = ede.getDrops();

                //for all the items dropped
                Iterator<ItemStack> iterator = drops.iterator();
                Collection<ItemStack> toAdd = new ArrayList<ItemStack>();
                while (iterator.hasNext()) {
                    ItemStack is = iterator.next();
                    //if it was a ghast tear drop the same amount of gold ingots
                    if (is.getType() == Material.GHAST_TEAR) {
                        iterator.remove();
                        toAdd.add(new ItemStack(Material.GOLD_INGOT, is.getAmount()));
                    }
                }
                drops.addAll(toAdd);
            }
        }
    }

    @Override
    public void enableFeature() {
    }

    @Override
    public void disableFeature() {
    }
}
