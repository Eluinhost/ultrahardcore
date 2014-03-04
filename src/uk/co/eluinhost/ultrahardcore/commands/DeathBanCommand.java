package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.features.IFeature;
import uk.co.eluinhost.ultrahardcore.features.deathbans.DeathBansFeature;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.ultrahardcore.util.WordsUtil;

public class DeathBanCommand {

    public static final String DEATH_BAN_BAN = "UHC.deathban.unban";
    public static final String DEATH_BAN_UNBAN = "UHC.deathban.ban";

    /**
     * Ran on /deathban
     * @param request the request params
     */
    @Command(trigger = "deathban",
            identifier = "DeathBanCommand")
    public void onDeathBanCommand(CommandRequest request){
        //TODO show syntax?
    }

    /**
     * Ran on /deathban unban {player}
     * @param request the request params
     */
    @Command(trigger = "unban",
            identifier = "DeathBanUnbanCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = DEATH_BAN_UNBAN)
    public void onDeathBanUnbanCommand(CommandRequest request){
        IFeature feature = FeatureManager.getInstance().getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(ChatColor.RED+"The deathbans feature isn't loaded!");
            return;
        }
        int amount = ((DeathBansFeature)feature).removeBan(request.getFirstArg());
        request.sendMessage(ChatColor.GOLD + "Removed " + amount + " bans for player " + request.getFirstArg());
    }

    /**
     * Ran on /deathban unban {player} {time}
     * @param request the request params
     */
    @Command(trigger = "unban",
            identifier = "DeathBanBanCommand",
            minArgs = 2,
            maxArgs = 2,
            permission = DEATH_BAN_BAN)
    public void onDeathBanBanCommand(CommandRequest request){
        IFeature feature = FeatureManager.getInstance().getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(ChatColor.RED+"The deathbans feature isn't loaded!");
            return;
        }
        String playername = request.getFirstArg();
        long duration = request.parseDuration(1);
        ((DeathBansFeature)feature).banPlayer(Bukkit.getOfflinePlayer(playername), "You are under a death ban, you will be unbanned in %timeleft", duration);
        request.sendMessage(ChatColor.GOLD + "Banned player " + playername + " for " + WordsUtil.formatTimeLeft(System.currentTimeMillis() + duration));
    }
}
