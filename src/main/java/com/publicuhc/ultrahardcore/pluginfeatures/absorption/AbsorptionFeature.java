package com.publicuhc.ultrahardcore.pluginfeatures.absorption;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public class AbsorptionFeature extends UHCFeature {

    /**
     * Construct a new feature
     *
     * @param plugin        the plugin to use
     * @param configManager the config manager to use
     * @param translate     the translator
     */
    @Inject
    protected AbsorptionFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * Runs on a player eating
     *
     * @param pee PlayerItemConsumeEvent
     */
    @EventHandler
    public void onPlayerEatEvent(PlayerItemConsumeEvent pee) {
        //if we're not enabled remove the absorption on the next tick
        if (!isEnabled()) {
            //if they ate a golden apple
            ItemStack is = pee.getItem();
            if (is.getType() == Material.GOLDEN_APPLE) {
                //remove the absorption effect for the player on the next tick
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new RemovePotionEffectRunnable(pee.getPlayer().getUniqueId(), PotionEffectType.ABSORPTION));
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "Absorption";
    }

    @Override
    public String getDescription() {
        return "Allows absorption from eating golden apples";
    }
}
