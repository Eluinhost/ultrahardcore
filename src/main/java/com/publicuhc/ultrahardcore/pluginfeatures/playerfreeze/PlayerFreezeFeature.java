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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Singleton
public class PlayerFreezeFeature extends UHCFeature {

    private final Map<UUID, Entity> m_entityMap = new HashMap<UUID, Entity>();
    private boolean m_globalMode = false;

    private final Collection<UUID> m_allowNextEvent = new ArrayList<UUID>();

    /**
     * handles frozen players
     * @param plugin the plugin
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private PlayerFreezeFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);
    }

    public void allowNextEvent(UUID player) {
        m_allowNextEvent.add(player);
    }

    /**
     * @param player the entity to freeze
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public void addPlayer(Player player){
        if(player.hasPermission(FreezeCommand.ANTIFREEZE_PERMISSION)) {
            return;
        }
        if(m_entityMap.containsKey(player.getUniqueId())) {
            return;
        }
        LivingEntity pig = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
        pig.setPassenger(player);
        pig.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true));
        pig.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 5, true));
        pig.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 5, true));
        m_entityMap.put(player.getUniqueId(), pig);
    }

    /**
     * @param playerID the player id
     */
    public void removePlayer(UUID playerID){
        removePig(playerID);
        m_entityMap.remove(playerID);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent ede) {
        if(m_entityMap.containsKey(ede.getEntity().getUniqueId())) {
            ede.setCancelled(true);
        }
    }

    /**
     * remove the pig for the name
     * @param playerID the player ID
     */
    private void removePig(UUID playerID){
        if(m_entityMap.containsKey(playerID)){
            Entity pig = m_entityMap.get(playerID);
            pig.eject();
            pig.remove();
            m_entityMap.put(playerID, null);
        }
    }

    /**
     * Called when a living entity tries to exit a vehicle
     * @param vee the vechile exit event
     */
    @EventHandler
    public void onVehicleDismountEvent(VehicleExitEvent vee) {
        LivingEntity entity = vee.getExited();
        if(m_allowNextEvent.contains(entity.getUniqueId())) {
            m_allowNextEvent.remove(entity.getUniqueId());
            vee.setCancelled(false);
            return;
        }
        if(entity instanceof Player){
            if(m_entityMap.containsKey(entity.getUniqueId())){
                vee.setCancelled(true);
            }
        }
    }

    /**
     * Remove all from the frozen list and sets global off
     */
    public void unfreezeAll(){
        m_globalMode = false;
        for(UUID playerID : m_entityMap.keySet()){
            removePlayer(playerID);
        }
    }

    /**
     * Adds all to the list and sets global on
     */
    public void freezeAll(){
        m_globalMode = true;
        for(Player p : Bukkit.getOnlinePlayers()){
            addPlayer(p);
        }
    }

    /**
     * Whenever a player joins
     * @param pje the player join event
     */
    @EventHandler( priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent pje){
        if(m_globalMode || m_entityMap.keySet().contains(pje.getPlayer().getUniqueId())){
            addPlayer(pje.getPlayer());
        }else{
            removePlayer(pje.getPlayer().getUniqueId());
        }
    }

    /**
     * When a player logs out remove the pig
     * @param pqe the quit event
     */
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent pqe){
        if(m_entityMap.containsKey(pqe.getPlayer().getUniqueId())){
            removePig(pqe.getPlayer().getUniqueId());
        }
    }

    /**
     * Called when the feature is being disabled
     */
    @Override
    protected void disableCallback(){
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
