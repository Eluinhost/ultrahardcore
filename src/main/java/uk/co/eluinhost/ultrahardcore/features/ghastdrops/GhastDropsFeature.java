package uk.co.eluinhost.ultrahardcore.features.ghastdrops;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Handles conversion of ghast tears to gold ingots
 * Config is whitelist type world dependant
 * Nothing special to do on disable and enable
 *
 * @author Graham
 */
@Singleton
public class GhastDropsFeature extends UHCFeature {

    /**
     * Stops ghasts dropping tears to get rid of regen potions
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    private GhastDropsFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
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
            if (featureEnabledForWorld(getBaseConfig(), ede.getEntity().getWorld().getName())) {

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
    public boolean featureEnabledForWorld(String featureNode, String worldName) {
        FileConfiguration config = getConfigManager().getConfig();
        //w&&f = enabled
        //w&&!f = disabled
        //!w&&f = disabled
        //!w&&!f = enabled
        // = AND
        boolean whitelist = config.getBoolean(featureNode + ".whitelist");
        boolean found = config.getStringList(featureNode + ".worlds").contains(worldName);
        return !(whitelist ^ found);
    }

    @Override
    public String getFeatureID() {
        return "GhastDrops";
    }

    @Override
    public String getDescription() {
        return "Ghasts drop golden ingots instead of tears";
    }
}
