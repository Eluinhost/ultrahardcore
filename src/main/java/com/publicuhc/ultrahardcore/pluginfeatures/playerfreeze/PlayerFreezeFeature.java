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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.logging.Level;

@Singleton
public class PlayerFreezeFeature extends UHCFeature {

    private boolean m_globalMode = false;
    private final List<PotionEffect> m_effects = new ArrayList<PotionEffect>();

    private FreezeRunnable m_freezer;
    private final PluginLogger m_logger;

    /**
     * handles frozen players
     *
     * @param plugin        the plugin
     * @param configManager the config manager
     * @param translate     the translator
     */
    @Inject
    private PlayerFreezeFeature(Plugin plugin, Configurator configManager, Translate translate, PluginLogger logger) {
        super(plugin, configManager, translate);
        m_logger = logger;
        init();
    }

    private void init() {
        List<String> potionEffectsList = getConfigManager().getConfig("main").getStringList(getBaseConfig() + "potion.effects");
        int duration = getConfigManager().getConfig("main").getInt(getBaseConfig() + "potion.duration");
        for (String potionEffectString : potionEffectsList) {
            String[] parts = potionEffectString.split(":");
            if (parts.length != 2) {
                m_logger.log(Level.SEVERE, "Potion effect " + potionEffectString + " does not contain a ':', skipping it.");
                continue;
            }

            int amplifier = -1;
            try {
                amplifier = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {}

            if (amplifier < 0) {
                m_logger.log(Level.SEVERE, "Potion effect " + potionEffectString + " has an invalid potion effect level '" + parts[1] + "', skipping it");
                continue;
            }

            PotionEffectType type = PotionEffectType.getByName(parts[0]);

            if (null == type) {
                m_logger.log(Level.SEVERE, "Potion effect " + potionEffectString + " has an invalid potion effect type '" + parts[0] + "', skipping it");
                continue;
            }

            m_effects.add(new PotionEffect(type, duration, amplifier, true));
        }

        m_freezer = new FreezeRunnable(m_effects);
        Bukkit.getPluginManager().registerEvents(m_freezer, getPlugin());
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent pte) {
        if( pte.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN ) {
            if (m_freezer.isPlayerFrozen(pte.getPlayer())) {
                m_freezer.addPlayer(pte.getPlayer());
            }
        }
    }

    /**
     * @param player the entity to freeze
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public void addPlayer(Player player) {
        if (player.hasPermission(FreezeCommand.ANTIFREEZE_PERMISSION)) {
            return;
        }
        if (m_freezer.isPlayerFrozen(player)) {
            return;
        }
        m_freezer.addPlayer(player);
    }

    public void removePlayer(Player player) {
        m_freezer.removePlayer(player);
    }

    /**
     * @param playerID the player id
     */
    public void removePlayer(UUID playerID) {
        m_freezer.removePlayer(playerID);
    }

    public boolean isPlayerFrozen(Player player) {
        return m_freezer.isPlayerFrozen(player);
    }

    public boolean isPlayerFrozen(UUID uuid) {
        return m_freezer.isPlayerFrozen(uuid);
    }

    /**
     * Remove all from the frozen list and sets global off
     */
    public void unfreezeAll() {
        m_globalMode = false;
        m_freezer.clear();
    }

    /**
     * Adds all to the list and sets global on
     */
    public void freezeAll() {
        m_globalMode = true;
        m_freezer.addPlayers(Bukkit.getOnlinePlayers());
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
        if (m_globalMode || m_freezer.isPlayerFrozen(pje.getPlayer())) {
            addPlayer(pje.getPlayer());
        } else {
            removePlayer(pje.getPlayer());
        }
    }

    /**
     * Called when the feature is being disabled
     */
    @Override
    protected void disableCallback() {
        Bukkit.getScheduler().cancelTask(m_freezer.getTaskId());
    }

    @Override
    protected void enableCallback() {
        m_freezer.runTaskTimer(getPlugin(), 0, getConfigManager().getConfig("main").getInt(getBaseConfig() + "period"));
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
