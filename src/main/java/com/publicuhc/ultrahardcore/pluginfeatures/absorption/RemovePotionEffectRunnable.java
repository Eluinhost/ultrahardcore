package com.publicuhc.ultrahardcore.pluginfeatures.absorption;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * When run removes absorption effect from the player UUID supplied
 */
public class RemovePotionEffectRunnable implements Runnable {

    private final UUID m_playerID;
    private final PotionEffectType[] m_effects;

    /**
     * Removes potion effects from a player when ran
     *
     * @param playerID the player to run for
     * @param potions  the list of potion effects to remove
     */
    public RemovePotionEffectRunnable(UUID playerID, PotionEffectType... potions) {
        m_playerID = playerID;
        m_effects = potions;
    }

    @Override
    public void run() {
        Player p = Bukkit.getPlayer(m_playerID);
        if (p != null) {
            for (PotionEffectType type : m_effects) {
                p.removePotionEffect(type);
            }
        }
    }
}
