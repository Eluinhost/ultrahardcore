/*
 * PlayerFreezeFeature.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.pluginfeatures.playerfreeze;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.commands.FreezeCommand;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PlayerFreezeFeature extends UHCFeature {

    private final Map<UUID, Location> m_entityMap = new HashMap<UUID, Location>();
    private boolean m_globalMode = false;

    /**
     * handles frozen players
     *
     * @param plugin        the plugin
     * @param configManager the config manager
     * @param translate     the translator
     */
    @Inject
    private PlayerFreezeFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    /**
     * @param player the entity to freeze
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public void addPlayer(Player player) {
        if (player.hasPermission(FreezeCommand.ANTIFREEZE_PERMISSION)) {
            return;
        }
        if (m_entityMap.containsKey(player.getUniqueId())) {
            return;
        }
        m_entityMap.put(player.getUniqueId(), player.getLocation());
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    /**
     * @param playerID the player id
     */
    public void removePlayer(UUID playerID) {
        m_entityMap.remove(playerID);
    }

    public boolean isPlayerFrozen(Player player) {
        return isPlayerFrozen(player.getUniqueId());
    }

    public boolean isPlayerFrozen(UUID uuid) {
        return m_entityMap.containsKey(uuid);
    }

    /**
     * Remove all from the frozen list and sets global off
     */
    public void unfreezeAll() {
        m_globalMode = false;
        for (UUID playerID : m_entityMap.keySet().toArray(new UUID[m_entityMap.size()])) {
            removePlayer(playerID);
        }
    }

    /**
     * Adds all to the list and sets global on
     */
    public void freezeAll() {
        m_globalMode = true;
        for (Player p : Bukkit.getOnlinePlayers()) {
            addPlayer(p);
        }
    }

    public boolean isGlobalMode() {
        return m_globalMode;
    }

    /**
     * Whenever a player joins
     *
     * @param pje the player join event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent pje) {
        if (m_globalMode || m_entityMap.keySet().contains(pje.getPlayer().getUniqueId())) {
            addPlayer(pje.getPlayer());
        } else {
            removePlayer(pje.getPlayer().getUniqueId());
        }
    }

    /**
     * Called when the feature is being disabled
     */
    @Override
    protected void disableCallback() {
        unfreezeAll();
    }

    @Override
    public String getFeatureID() {
        return "PlayerFreeze";
    }

    @Override
    public String getDescription() {
        return "Allows for freezing players in place";
    }
}
