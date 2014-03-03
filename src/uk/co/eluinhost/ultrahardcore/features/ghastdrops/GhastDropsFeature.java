package uk.co.eluinhost.ultrahardcore.features.ghastdrops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

/**
 * Handles conversion of ghast tears to gold ingots
 * Config is whitelist type world dependant
 * Nothing special to do on disable and enable
 *
 * @author Graham
 */
public class GhastDropsFeature extends UHCFeature {

    /**
     * Stops ghasts dropping tears to get rid of regen potions
     */
    public GhastDropsFeature() {
        super("GhastDrops","Ghasts drop golden ingots instead of tears");
    }

    /**
     * Whenever an entity dies
     * @param ede the death event
     */
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent ede) {
        //if we're enabled and a ghast died
        if (isEnabled() && ede.getEntityType() == EntityType.GHAST) {
            //if ghasts can't drop tears in this world
            if (featureEnabledForWorld(ConfigNodes.GHAST_DROP_CHANGES_NODE, ede.getEntity().getWorld().getName())) {

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

    /**
     *  Is the feature enabled for the world given
     * @param featureNode the feature name
     * @param worldName the world
     * @return true if allowed, false otherwise
     */
    public static boolean featureEnabledForWorld(String featureNode, String worldName) {
        FileConfiguration config = ConfigManager.getInstance().getConfig();
        //w&&f = enabled
        //w&&!f = disabled
        //!w&&f = disabled
        //!w&&!f = enabled
        // = AND
        boolean whitelist = config.getBoolean(featureNode + ".whitelist");
        boolean found = config.getStringList(featureNode + ".worlds").contains(worldName);
        return !(whitelist ^ found);
    }
}
