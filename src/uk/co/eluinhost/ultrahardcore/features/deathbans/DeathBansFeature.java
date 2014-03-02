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
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigType;
import uk.co.eluinhost.ultrahardcore.config.ConfigManager;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeathBansFeature extends UHCFeature {

    private List<DeathBan> m_deathBans = new ArrayList<DeathBan>();

    private static final Pattern BAN_LENGTH_PATTERN = Pattern.compile(
            "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
            Pattern.CASE_INSENSITIVE);

    private static final long MILLIS_PER_SECOND = 1000;
    private static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
    private static final long MILLIS_PER_HOUR   = MILLIS_PER_MINUTE * 60;
    private static final long MILLIS_PER_DAY    = MILLIS_PER_HOUR * 24;
    private static final long MILLIS_PER_WEEK   = MILLIS_PER_DAY * 7;
    private static final long MILLIS_PER_MONTH  = MILLIS_PER_DAY * 30;
    private static final long MILLIS_PER_YEAR   = MILLIS_PER_DAY * 365;

    public static final String BASE_DEATH_BAN = BASE_PERMISSION + "deathban.";
    public static final String DEATH_BAN_IMMUNE = BASE_DEATH_BAN + "immune";

    //TODO more cleanup
    public DeathBansFeature() {
        super("DeathBans", "Bans a player on death for a specified amount of time");

        FileConfiguration banConfig = ConfigManager.getInstance().getConfig();

        @SuppressWarnings("unchecked")
        List<DeathBan> banList = (List<DeathBan>) banConfig.getList("bans",new ArrayList<Object>());
        for(DeathBan deathBan : banList){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.getName().equalsIgnoreCase(deathBan.getPlayerName())){
                    player.kickPlayer(deathBan.getGroupName().replaceAll("%timeleft", formatTimeLeft(deathBan.getUnbanTime())));
                }
            }
        }
        m_deathBans = banList;
    }

    public static String formatTimeLeft(long timeUnban){
        long duration = timeUnban - System.currentTimeMillis();
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        if(days > (long) Short.MAX_VALUE){
            return " forever";
        }
        duration -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);
        long mins = TimeUnit.MILLISECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(mins);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        StringBuilder sb = new StringBuilder();
        if(days > 0L){
            sb.append(" ").append(days).append(days == 1 ? " day" : " days");
        }
        if(hours > 0L){
            sb.append(" ").append(hours).append(hours == 1 ? " hour" : " hours");
        }
        if(mins > 0L){
            sb.append(" ").append(mins).append(mins == 1 ? " min" : " mins");
        }
        if(seconds > 0L){
            sb.append(" ").append(seconds).append(seconds == 1 ? " second" : " seconds");
        }
        return sb.toString();
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
        ConfigManager.getInstance().getConfig(ConfigType.BANS).set("bans", m_deathBans);
        ConfigManager.getInstance().saveConfig(ConfigType.BANS);
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

    public static long parseBanTime(String banTime) {
        if("infinite".equalsIgnoreCase(banTime)){
            return Long.MAX_VALUE/2;
        }
        long duration = 0;
        boolean match = false;
        Matcher mat = BAN_LENGTH_PATTERN.matcher(banTime);
        while (mat.find())    {
            if (mat.group() != null && !mat.group().isEmpty()) {
                for (int i = 0; i < mat.groupCount(); i++) {
                    if (mat.group(i) != null && !mat.group(i).isEmpty()) {
                        match = true;
                        break;
                    }
                }
                if (match){
                    if (mat.group(1) != null && !mat.group(1).isEmpty()){
                        duration += MILLIS_PER_YEAR * Integer.parseInt(mat.group(1));
                    }
                    if (mat.group(2) != null && !mat.group(2).isEmpty()){
                        duration += MILLIS_PER_MONTH * Integer.parseInt(mat.group(2));
                    }
                    if (mat.group(3) != null && !mat.group(3).isEmpty()){
                        duration += MILLIS_PER_WEEK * Integer.parseInt(mat.group(3));
                    }
                    if (mat.group(4) != null && !mat.group(4).isEmpty()){
                        duration += MILLIS_PER_DAY * Integer.parseInt(mat.group(4));
                    }
                    if (mat.group(5) != null && !mat.group(5).isEmpty()){
                        duration += MILLIS_PER_HOUR * Integer.parseInt(mat.group(5));
                    }
                    if (mat.group(6) != null && !mat.group(6).isEmpty()){
                        duration += MILLIS_PER_MINUTE * Integer.parseInt(mat.group(6));
                    }
                    if (mat.group(7) != null && !mat.group(7).isEmpty()){
                         duration+= MILLIS_PER_SECOND * Integer.parseInt(mat.group(7));
                    }
                    break;
                }
            }
        }
        return duration;
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
                UltraHardcore.getInstance(),
                new PlayerBanner(playerName, message, unbanTime),
                ConfigManager.getInstance().getConfig().getLong(ConfigNodes.DEATH_BANS_DELAY)
        );
        saveBans();
        UltraHardcore.getInstance().getLogger().info("Added " + offlinePlayer.getName() + " to temp ban list");
    }

    /**
     * Bans the player based on permissions/config options
     * @param p the player to ban
     */
    public void processBansForPlayer(Player p){
        ConfigurationSection banTypes = ConfigManager.getInstance().getConfig().getConfigurationSection(ConfigNodes.DEATH_BANS_CLASSES);
        Set<String> permissionNames = banTypes.getKeys(false);
        Logger logger = UltraHardcore.getInstance().getLogger();
        for(String permission : permissionNames){
            if(!p.hasPermission("UHC.deathban.group."+permission)){
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
                    long duration = parseBanTime(length);
                    banPlayer(p,message,duration);
                }else if("worldkick".equalsIgnoreCase(action)){
                    String world = type.getString("worldkick_world","NO WORLD IN CONFIG");
                    World w = Bukkit.getWorld(world);
                    if(w != null){
                        p.setBedSpawnLocation(w.getSpawnLocation());
                    }
                }else if("bungeekick".equalsIgnoreCase(action)){
                    String server =type.getString("bungeekick_server","NO SERVER SET");
                    ServerUtil.sendPlayerToServer(p,server);
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
                p.kickPlayer(m_message.replaceAll("%timeleft", formatTimeLeft(m_unbanTime)));
            }
        }
    }
}
