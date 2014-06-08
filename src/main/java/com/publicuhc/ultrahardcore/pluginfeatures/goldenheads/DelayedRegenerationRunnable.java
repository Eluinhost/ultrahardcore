package com.publicuhc.ultrahardcore.pluginfeatures.goldenheads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class DelayedRegenerationRunnable implements Runnable {

    public static final int TICKS_PER_HALF_HEART = 25;

    private final UUID m_playerID;
    private final int m_total;

    /**
     * Starts a regneration 2 effect for the given player when ran. Will overwrite any existing regeneration
     * @param total the total number of half hearts to heal
     */
    public DelayedRegenerationRunnable(int total, UUID playerID) {
        m_playerID = playerID;
        m_total = total;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(m_playerID);
        if(null == player) {
            return;
        }
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, TICKS_PER_HALF_HEART * m_total, 1));
    }
}
