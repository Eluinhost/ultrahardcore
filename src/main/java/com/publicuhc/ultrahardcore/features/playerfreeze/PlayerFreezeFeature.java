package com.publicuhc.ultrahardcore.features.playerfreeze;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.publicuhc.ultrahardcore.commands.FreezeCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.features.UHCFeature;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Singleton
public class PlayerFreezeFeature extends UHCFeature {

    private final Map<String, Entity> m_entityMap = new HashMap<String,Entity>();
    private boolean m_globalMode = false;

    /**
     * handles frozen players
     * @param plugin the plugin
     * @param configManager the config manager
     */
    @Inject
    private PlayerFreezeFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
    }

    /**
     * @param player the entity to freeze
     */
    public void addPlayer(Player player){
        LivingEntity pig = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
        pig.setPassenger(player);
        pig.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true));
        pig.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 5, true));
        pig.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 5, true));
        m_entityMap.put(player.getName(), pig);
    }

    /**
     * @param name the player name
     */
    public void removePlayer(String name){
        removePig(name);
        m_entityMap.remove(name);
    }

    /**
     * remove the chicken for the name
     * @param name the player name
     */
    private void removePig(String name){
        if(m_entityMap.containsKey(name)){
            Entity pig = m_entityMap.get(name);
            pig.setPassenger(null);
            pig.remove();
            m_entityMap.put(name,null);
        }
    }

    /**
     * Called when a living entity tries to exit a vehicle
     * @param vee the vechile exit event
     */
    @EventHandler
    public void onVehicleDismountEvent(VehicleExitEvent vee){
        LivingEntity entity = vee.getExited();
        if(entity instanceof Player){
            if(m_entityMap.containsKey(((Player)entity).getName())){
                vee.setCancelled(true);
            }
        }
    }

    /**
     * Remove all from the frozen list and sets global off
     */
    public void unfreezeAll(){
        m_globalMode = false;
        for(String s : m_entityMap.keySet()){
            removePlayer(s);
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
        if((m_globalMode || m_entityMap.keySet().contains(pje.getPlayer().getName()))
             && !pje.getPlayer().hasPermission(FreezeCommand.ANTIFREEZE_PERMISSION)){
            addPlayer(pje.getPlayer());
        }else{
            removePlayer(pje.getPlayer().getName());
        }
    }

    /**
     * When a player logs out remove the chicken
     * @param pqe the quit event
     */
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent pqe){
        if(m_entityMap.containsKey(pqe.getPlayer().getName())){
            removePig(pqe.getPlayer().getName());
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
