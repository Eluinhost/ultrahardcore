package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.features.IFeature;
import uk.co.eluinhost.ultrahardcore.features.deathbans.DeathBansFeature;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.ultrahardcore.util.WordsUtil;

public class DeathBanCommand extends SimpleCommand {

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
            parentID = "DeathBanCommand",
            permission = DEATH_BAN_UNBAN)
    public void onDeathBanUnbanCommand(CommandRequest request){
        IFeature feature = FeatureManager.getInstance().getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(translate("deathbans.not_loaded"));
            return;
        }
        int amount = ((DeathBansFeature)feature).removeBan(request.getFirstArg());
        request.sendMessage(translate("deathbans.removed").replaceAll("%amount%", String.valueOf(amount)).replaceAll("%name%", request.getFirstArg()));
    }

    /**
     * Ran on /deathban ban {player} {time}
     * @param request the request params
     */
    @Command(trigger = "ban",
            identifier = "DeathBanBanCommand",
            minArgs = 2,
            maxArgs = 2,
            parentID = "DeathBanCommand",
            permission = DEATH_BAN_BAN)
    public void onDeathBanBanCommand(CommandRequest request){
        IFeature feature = FeatureManager.getInstance().getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(translate("deathbans.not_loaded"));
            return;
        }
        String playername = request.getFirstArg();
        long duration = request.parseDuration(1);
        ((DeathBansFeature)feature).banPlayer(Bukkit.getOfflinePlayer(playername), translate("deathbans.ban_message"), duration);
        request.sendMessage(translate("deathbans.banned").replaceAll("%name%",playername).replaceAll("%time%",WordsUtil.formatTimeLeft(System.currentTimeMillis() + duration)));
    }
}
