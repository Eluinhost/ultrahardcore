package uk.co.eluinhost.ultrahardcore.features.deathbans;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;
import uk.co.eluinhost.ultrahardcore.util.WordsUtil;

import java.util.*;
import java.util.logging.Logger;

public class DeathBansFeature extends UHCFeature {

    private List<DeathBan> m_deathBans = new ArrayList<DeathBan>();

    public static final String BASE_DEATH_BAN = BASE_PERMISSION + "deathban.";
    public static final String DEATH_BAN_IMMUNE = BASE_DEATH_BAN + "immune";
    public static final String BASE_GROUP = BASE_DEATH_BAN + "group.";

    private final long m_banDelay;

    public static final String CLASSES_NODE = "classes";

    /**
     * Bans players on death
     */
    public DeathBansFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, "DeathBans", "Bans a player on death for a specified amount of time", configManager);

        FileConfiguration banConfig = configManager.getConfig();

        @SuppressWarnings("unchecked")
        List<DeathBan> banList = (List<DeathBan>) banConfig.getList("bans",new ArrayList<Object>());
        for(DeathBan deathBan : banList){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.getName().equalsIgnoreCase(deathBan.getPlayerName())){
                    player.kickPlayer(deathBan.getGroupName().replaceAll("%timeleft", new WordsUtil(configManager).formatTimeLeft(deathBan.getUnbanTime())));
                }
            }
        }
        m_deathBans = banList;

        m_banDelay = configManager.getConfig().getLong(getBaseConfig()+"delay");
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
     * @param playerName the player name to unban
     * @return amount of bans removed
     */
    public int removeBan(String playerName){
        Iterator<DeathBan> iterator = m_deathBans.iterator();
        int amount = 0;
        while(iterator.hasNext()){
            DeathBan deathBan = iterator.next();
            if(deathBan.getPlayerName().equals(playerName)){
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
                    removeBan(deathBan.getPlayerName());
                }
            }
        }
    }

    /**
     * Ban the player
     * @param offlinePlayer the player to ban
     * @param message the message to ban them with
     * @param duration how long in millis to ban them for
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public void banPlayer(OfflinePlayer offlinePlayer, String message, long duration){
        long unbanTime = System.currentTimeMillis()+duration;
        DeathBan db = new DeathBan(offlinePlayer.getName(),unbanTime, message);
        m_deathBans.add(db);
        String playerName = offlinePlayer.getName();
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                getPlugin(),
                new PlayerBanner(playerName, message, unbanTime),
                m_banDelay
        );
        saveBans();
    }

    /**
     * Bans the player based on permissions/config options
     * @param p the player to ban
     */
    public void processBansForPlayer(Player p){
        ConfigurationSection banTypes = getConfigManager().getConfig().getConfigurationSection(getBaseConfig()+CLASSES_NODE);
        Set<String> permissionNames = banTypes.getKeys(false);
        Logger logger = getPlugin().getLogger();
        for(String permission : permissionNames){
            if(!p.hasPermission(BASE_GROUP+permission)){
                continue;
            }
            List<String> actions = banTypes.getStringList(permission + ".actions");
            ConfigurationSection type = banTypes.getConfigurationSection(permission);
            for(String action : actions){
                //TODO clean up
                if("serverkick".equalsIgnoreCase(action)){
                    String kickMessage = type.getString("serverkick_message","NO SERVER KICK MESSAGE SET IN CONFIG FILE");
                    p.kickPlayer(kickMessage);
                }else if("serverban".equalsIgnoreCase(action)) {
                    String length = type.getString("serverban_duration","1s");
                    String message = type.getString("serverban_message","NO BAN MESSAGE SET IN CONFIG FILE");
                    long duration = new WordsUtil(getConfigManager()).parseTime(length);
                    banPlayer(p,message,duration);
                }else if("worldkick".equalsIgnoreCase(action)){
                    String world = type.getString("worldkick_world","NO WORLD IN CONFIG");
                    World w = Bukkit.getWorld(world);
                    if(w != null){
                        p.setBedSpawnLocation(w.getSpawnLocation());
                    }
                }else if("bungeekick".equalsIgnoreCase(action)){
                    String server =type.getString("bungeekick_server","NO SERVER SET");
                    ServerUtil.sendPlayerToServer(getPlugin(),p,server);
                }else{
                    logger.severe("Error in deathbans config, action '"+action+"' unknown");
                }
            }
            return;
        }
    }

    private static class PlayerBanner implements Runnable {
        private final String m_playerName;
        private final String m_message;
        private final long m_unbanTime;

        /**
         * Bans the player when ran
         * @param playerName the player name to ban
         * @param message the message to ban them with
         * @param unbanTime the time to unban them again
         */
        PlayerBanner(String playerName, String message, long unbanTime) {
            m_playerName = playerName;
            m_message = message;
            m_unbanTime = unbanTime;
        }

        @Override
        public void run() {
            OfflinePlayer op = Bukkit.getOfflinePlayer(m_playerName);
            Player p = op.getPlayer();
            if (p != null) {
                p.kickPlayer(m_message.replaceAll("%timeleft%", WordsUtil.formatTimeLeft(m_unbanTime)));
            }
        }
    }
}
