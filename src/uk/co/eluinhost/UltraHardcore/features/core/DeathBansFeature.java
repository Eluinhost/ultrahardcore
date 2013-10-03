package uk.co.eluinhost.UltraHardcore.features.core;

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
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.bans.DeathBan;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;
import uk.co.eluinhost.UltraHardcore.util.ServerUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeathBansFeature extends UHCFeature {

    private List<DeathBan> bans = new ArrayList<DeathBan>();
    private static Pattern pat = Pattern.compile(
            "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
            Pattern.CASE_INSENSITIVE);

    public DeathBansFeature(boolean enabled) {
		super(enabled);
		setFeatureID("DeathBans");
		setDescription("Bans a player on death for a specified amount of time");

        FileConfiguration banConfig = ConfigHandler.getConfig(ConfigHandler.BANS);

        @SuppressWarnings("unchecked")
        List<DeathBan> f_bans = (List<DeathBan>) banConfig.getList("bans",new ArrayList<Object>());
        for(DeathBan d : f_bans){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getName().equalsIgnoreCase(d.getPlayerName())){
                    p.kickPlayer(d.getGroupName().replaceAll("%timeleft",formatTimeLeft(d.getUnbanTime())));
                }
            }
        }
        bans = f_bans;
	}

    public static String formatTimeLeft(long timeUnban){
        long duration = timeUnban - System.currentTimeMillis();

        long days = TimeUnit.MILLISECONDS.toDays(duration);
        if(days > Short.MAX_VALUE){
            return " forever";
        }
        duration -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);
        long mins = TimeUnit.MILLISECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(mins);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        StringBuilder sb = new StringBuilder();
        if(days > 0){
            sb.append(" ");
            sb.append(days);
            if(days == 1)
                sb.append(" day");
            else
                sb.append(" days");
        }
        if(hours > 0){
            sb.append(" ");
            sb.append(hours);
            if(hours == 1)
                sb.append(" hour");
            else
                sb.append(" hours");
        }
        if(mins > 0){
            sb.append(" ");
            sb.append(mins);
            if(mins == 1)
                sb.append(" min");
            else
                sb.append(" mins");
        }
        if(seconds > 0){
            sb.append(" ");
            sb.append(seconds);
            if(seconds ==1)
                sb.append(" second");
            else
                sb.append(" seconds");
        }
        return sb.toString();
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent pde){
         if(isEnabled()){
             if(pde.getEntity().hasPermission(PermissionNodes.DEATH_BAN_IMMUNE)){
                 return;
             }
             processBansForPlayer(pde.getEntity());
         }
    }

    public int removeBan(String playerName){
        Iterator<DeathBan> i = bans.iterator();
        int amount = 0;
        while(i.hasNext()){
            DeathBan d = i.next();
            if(d.getPlayerName().equals(playerName)){
                i.remove();
                amount++;
            }
        }
        saveBans();
        return amount;
    }

    private void saveBans(){
        ConfigHandler.getConfig(ConfigHandler.BANS).set("bans",bans);
        ConfigHandler.saveConfig(ConfigHandler.BANS);
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent ple){
        if(isEnabled()){
            for(DeathBan d : bans){
                if(d.getPlayerName().equalsIgnoreCase(ple.getPlayer().getName())){
                    if(System.currentTimeMillis() >= d.getUnbanTime()){
                        removeBan(ple.getPlayer().getName());
                        return;
                    }
                    ple.disallow(PlayerLoginEvent.Result.KICK_BANNED, d.getGroupName().replaceAll("%timeleft", formatTimeLeft(d.getUnbanTime())));
                }
            }
        }
    }

    public static long parseBanTime(String banTime) {
        if(banTime.equalsIgnoreCase("infinite")){
            return Long.MAX_VALUE/2;
        }
        long duration = 0;
        boolean match = false;
        Matcher m = pat.matcher(banTime);
        while (m.find())    {
            if ((m.group() != null) && (!m.group().isEmpty())) {
                for (int i = 0; i < m.groupCount(); i++) {
                    if ((m.group(i) != null) && (!m.group(i).isEmpty())) {
                        match = true;
                        break;
                    }
                }

                if (match){
                    if ((m.group(1) != null) && (!m.group(1).isEmpty())){
                        duration +=   1000*60*24*365*Integer.parseInt(m.group(1));
                    }
                    if ((m.group(2) != null) && (!m.group(2).isEmpty())){
                        duration +=    1000*60*24*7*30*Integer.parseInt(m.group(2));
                    }
                    if ((m.group(3) != null) && (!m.group(3).isEmpty())){
                        duration +=  1000*60*60*24*7*Integer.parseInt(m.group(3));
                    }
                    if ((m.group(4) != null) && (!m.group(4).isEmpty())){
                        duration +=     1000*60*60*24*Integer.parseInt(m.group(4));
                    }
                    if ((m.group(5) != null) && (!m.group(5).isEmpty())){
                        duration +=     1000*60*60*Integer.parseInt(m.group(5));
                    }
                    if ((m.group(6) != null) && (!m.group(6).isEmpty())){
                        duration +=     1000*60*  Integer.parseInt(m.group(6));
                    }
                    if ((m.group(7) != null) && (!m.group(7).isEmpty())){
                         duration+=   1000* Integer.parseInt(m.group(7));
                    }
                    break;
                }
            }
        }
        return duration;
    }

    public void banPlayer(OfflinePlayer p, final String message, long duration){
        final long unban_time = System.currentTimeMillis()+duration;
        DeathBan db = new DeathBan(p.getName(),unban_time, message);
        bans.add(db);
        final String player_name = p.getName();
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(),
                new Runnable() {
                    @Override
                    public void run() {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(player_name);
                        Player p = op.getPlayer();
                        if (p != null) {
                            p.kickPlayer(message.replaceAll("%timeleft",formatTimeLeft(unban_time)));
                        }
                    }
                }
                , ConfigHandler.getConfig(ConfigHandler.MAIN).getLong(ConfigNodes.DEATH_BANS_DELAY));
        saveBans();
        UltraHardcore.getInstance().getLogger().info("Added "+p.getName()+" to temp ban list");
    }

    public void processBansForPlayer(final Player p){
        ConfigurationSection ban_types = ConfigHandler.getConfig(ConfigHandler.MAIN).getConfigurationSection(ConfigNodes.DEATH_BANS_CLASSES);
        Set<String> permission_names = ban_types.getKeys(false);
        Logger logger = UltraHardcore.getInstance().getLogger();
        for(String permission : permission_names){
            if(!p.hasPermission("UHC.deathban.group."+permission)){
                continue;
            }
            List<String> actions = ban_types.getStringList(permission + ".actions");
            ConfigurationSection type = ban_types.getConfigurationSection(permission);
            for(String action : actions){
                if(action.equalsIgnoreCase("serverkick")){
                    String kick_message = type.getString("serverkick_message","NO SERVER KICK MESSAGE SET IN CONFIG FILE");
                    p.kickPlayer(kick_message);
                    logger.info("Kicked "+p.getName()+" from the server");
                }else if(action.equalsIgnoreCase("serverban")) {
                    String length = type.getString("serverban_duration","1s");
                    String message = type.getString("serverban_message","NO BAN MESSAGE SET IN CONFIG FILE");
                    long duration = parseBanTime(length);
                    banPlayer(p,message,duration);
                }else if(action.equalsIgnoreCase("worldkick")){
                    String world = type.getString("worldkick_world","NO WORLD IN CONFIG");
                    World w = Bukkit.getWorld(world);
                    if(w == null){
                        logger.severe("World "+world+" is not a valid world to kick a player to");
                    }else{
                        p.setBedSpawnLocation(w.getSpawnLocation());
                        logger.info(p.getName()+" will respawn at the spawn of the world "+world);
                    }
                }else if(action.equalsIgnoreCase("bungeekick")){
                    String server =type.getString("bungeekick_server","NO SERVER SET");
                    ServerUtil.sendPlayerToServer(p,server);
                    logger.info("Sent "+p.getName()+" to the server "+server);
                }else{
                    logger.severe("Error in deathbans config, action '"+action+"' unknown");
                }
            }
            return;
        }
    }

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}

}
