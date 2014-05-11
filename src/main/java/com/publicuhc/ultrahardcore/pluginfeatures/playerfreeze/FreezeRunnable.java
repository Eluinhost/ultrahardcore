package com.publicuhc.ultrahardcore.pluginfeatures.playerfreeze;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FreezeRunnable extends BukkitRunnable implements Listener {

    private final List<PotionEffect> m_effects;
    private boolean enabled;
    private final Map<UUID, Location> m_playerMap = new HashMap<UUID, Location>();

    public FreezeRunnable(List<PotionEffect> effects) {
        m_effects = effects;
    }

    public void setEnabled(boolean enabled) {

    }

    public void addPlayer(Player player) {
        addPlayer(player.getUniqueId(), player.getLocation());
    }

    public void addPlayer(UUID player, Location location) {
        m_playerMap.put(player, location);
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    public void removePlayer(UUID player) {
        m_playerMap.remove(player);
    }

    public boolean isPlayerFrozen(Player player) {
        return isPlayerFrozen(player.getUniqueId());
    }

    public boolean isPlayerFrozen(UUID uuid) {
        return m_playerMap.containsKey(uuid);
    }

    public void clear() {
        m_playerMap.clear();
    }

    public void addPlayers(Player... players) {
        for(Player player : players) {
            addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent pme) {
        //check if we're running
        try {
            getTaskId();
        } catch (IllegalStateException ignored) {
            return;
        }

        Player player = pme.getPlayer();
        if (isPlayerFrozen(player)) {
            Location newLocation = pme.getTo();
            Location storedLocation = m_playerMap.get(player.getUniqueId());
            storedLocation.setPitch(newLocation.getPitch());
            storedLocation.setYaw(newLocation.getYaw());
            pme.setTo(storedLocation);
        }
    }

    @Override
    public void run() {
        for(Map.Entry<UUID, Location> entry : m_playerMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (null == player) {
                continue;
            }

            for (PotionEffect effect : m_effects) {
                player.addPotionEffect(effect, true);
            }
        }
    }
}
