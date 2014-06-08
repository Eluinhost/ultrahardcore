package com.publicuhc.ultrahardcore.pluginfeatures.absorption;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * When run removes absorption effect from the player name supplied
 */
public class RemoveAbsorptionRunnable implements Runnable{

    private final UUID m_playerID;

    /**
     * Removes absorption from a player when ran
     * @param playerID the player to run for
     */
    public RemoveAbsorptionRunnable(UUID playerID){
        m_playerID = playerID;
    }

    @Override
    public void run() {
        Player p = Bukkit.getPlayer(m_playerID);
        if(p != null){
            p.removePotionEffect(PotionEffectType.ABSORPTION);
        }
    }
}
