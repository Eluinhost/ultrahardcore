/*
 * DeathBansFeature.java
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

package com.publicuhc.ultrahardcore.pluginfeatures.deathbans;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import com.publicuhc.ultrahardcore.util.WordsUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Logger;

@Singleton
public class DeathBansFeature extends UHCFeature {

    private List<DeathBan> m_deathBans = new ArrayList<DeathBan>();

    public static final String BASE_DEATH_BAN = BASE_PERMISSION + "deathban.";
    public static final String DEATH_BAN_IMMUNE = BASE_DEATH_BAN + "immune";
    public static final String BASE_GROUP = BASE_DEATH_BAN + "group.";

    private final long m_banDelay;

    public static final String CLASSES_NODE = "classes";

    /**
     * Bans players on death
     * @param configManager the config manager
     * @param translate the translator
     * @param plugin the plugin
     */
    @Inject
    private DeathBansFeature(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);

        FileConfiguration banConfig = configManager.getConfig("main");

        @SuppressWarnings("unchecked")
        List<DeathBan> banList = (List<DeathBan>) banConfig.getList("bans",new ArrayList<Object>());
        for(DeathBan deathBan : banList){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.getUniqueId().equals(deathBan.getPlayerID())){
                    player.kickPlayer(deathBan.getGroupName().replaceAll("%timeleft", WordsUtil.formatTimeLeft(deathBan.getUnbanTime())));
                }
            }
        }
        m_deathBans = banList;

        m_banDelay = configManager.getConfig("main").getLong(getBaseConfig()+"delay");
    }

    /**
     * When a player is killed
     * @param pde the death event
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent pde){
         if(isEnabled()){
             if(pde.getEntity().hasPermission(DEATH_BAN_IMMUNE)){
                 return;
             }
             processBansForPlayer(pde.getEntity());
         }
    }

    /**
     * Removes all bans for the playername
     * @param playerID the player name to unban
     * @return amount of bans removed
     */
    public int removeBan(UUID playerID){
        Iterator<DeathBan> iterator = m_deathBans.iterator();
        int amount = 0;
        while(iterator.hasNext()){
            DeathBan deathBan = iterator.next();
            if(deathBan.getPlayerID().equals(playerID)){
                iterator.remove();
                amount++;
            }
        }
        saveBans();
        return amount;
    }

    /**
     * Save all the bans to file
     */
    private void saveBans(){
        getConfigManager().getConfig("bans").set("bans", m_deathBans);
        getConfigManager().saveConfig("bans");
    }

    /**
     * When a player logs in
     * @param ple player login event
     */
    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent ple){
        if(isEnabled()){
            for(DeathBan deathBan : m_deathBans){
                if(deathBan.processPlayerLoginEvent(ple)){
                    removeBan(deathBan.getPlayerID());
                }
            }
        }
    }

    /**
     * Ban the player
     * @param playerID the player to ban
     * @param message the message to ban them with
     * @param duration how long in millis to ban them for
     */
    public void banPlayer(UUID playerID, String message, long duration){
        long unbanTime = System.currentTimeMillis()+duration;
        DeathBan db = new DeathBan(playerID, unbanTime, message);
        m_deathBans.add(db);
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                getPlugin(),
                new PlayerBanner(playerID, message, unbanTime),
                m_banDelay
        );
        saveBans();
    }

    /**
     * Bans the player based on permissions/config options
     * @param p the player to ban
     */
    public void processBansForPlayer(Player p){
        ConfigurationSection banTypes = getConfigManager().getConfig("main").getConfigurationSection(getBaseConfig()+CLASSES_NODE);
        Set<String> permissionNames = banTypes.getKeys(false);
        Logger logger = getPlugin().getLogger();
        for(String permission : permissionNames){
            if(!p.hasPermission(BASE_GROUP+permission)){
                continue;
            }
            List<String> actions = banTypes.getStringList(permission + ".actions");
            ConfigurationSection type = banTypes.getConfigurationSection(permission);
            for(String action : actions){

                if("serverkick".equalsIgnoreCase(action)){
                    String kickMessage = type.getString("serverkick_message");
                    if(null == kickMessage) {
                        kickMessage = "NO SERVER KICK MESSAGE SET IN CONFIG FILE";
                    }
                    p.kickPlayer(kickMessage);
                    continue;
                }

                if("serverban".equalsIgnoreCase(action)) {
                    String length = type.getString("serverban_duration");
                    if(null == length) {
                        length = "1s";
                    }
                    String message = type.getString("serverban_message");
                    if(null == message) {
                        message = "NO BAN MESSAGE SET IN CONFIG FILE";
                    }
                    long duration = WordsUtil.parseTime(length);
                    banPlayer(p.getUniqueId(), message, duration);
                    continue;
                }

                if("worldkick".equalsIgnoreCase(action)){
                    String world = type.getString("worldkick_world");
                    if(null == world) {
                        logger.severe("Error in deathbans during worldkick, node 'worldkick_world' not set");
                        continue;
                    }
                    World w = Bukkit.getWorld(world);
                    if(w != null){
                        p.setBedSpawnLocation(w.getSpawnLocation());
                    }
                    continue;
                }

                if("bungeekick".equalsIgnoreCase(action)){
                    String server = type.getString("bungeekick_server");
                    if(null == server) {
                        logger.severe("Error in deathbans during bungeekick, node 'bungeekick_server' not set");
                        continue;
                    }
                    ServerUtil.sendPlayerToServer(getPlugin(), p, server);
                    continue;
                }

                logger.severe("Error in deathbans config, action '"+action+"' unknown");
            }
            return;
        }
    }

    @Override
    public String getFeatureID() {
        return "DeathBans";
    }

    @Override
    public String getDescription() {
        return "Bans a player on death for a specified amount of time";
    }

    private static class PlayerBanner implements Runnable {
        private final UUID m_playerID;
        private final String m_message;
        private final long m_unbanTime;

        /**
         * Bans the player when ran
         * @param playerID the player to ban
         * @param message the message to ban them with
         * @param unbanTime the time to unban them again
         */
        PlayerBanner(UUID playerID, String message, long unbanTime) {
            m_playerID = playerID;
            m_message = message;
            m_unbanTime = unbanTime;
        }

        @Override
        public void run() {
            Player p = Bukkit.getPlayer(m_playerID);
            if (p != null) {
                p.kickPlayer(m_message.replaceAll("%timeleft%", WordsUtil.formatTimeLeft(m_unbanTime)));
            }
        }
    }
}
