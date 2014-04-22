package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.features.FeatureManager;
import com.publicuhc.features.IFeature;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.deathbans.DeathBansFeature;
import com.publicuhc.ultrahardcore.util.WordsUtil;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class DeathBanCommand extends SimpleCommand {

    public static final String DEATH_BAN_BAN = "UHC.deathban.unban";
    public static final String DEATH_BAN_UNBAN = "UHC.deathban.ban";

    private final FeatureManager m_features;

    /**
     * @param configManager the config manager
     * @param translate the translator
     * @param features the feature manager
     */
    @Inject
    private DeathBanCommand(Configurator configManager, Translate translate, FeatureManager features) {
        super(configManager, translate);
        m_features = features;
    }


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
        IFeature feature = m_features.getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(translate("deathbans.not_loaded", locale(request.getSender())));
            return;
        }
        int amount = ((DeathBansFeature)feature).removeBan(request.getFirstArg());
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("amount", String.valueOf(amount));
        vars.put("name", request.getFirstArg());
        request.sendMessage(translate("deathbans.removed", locale(request.getSender()), vars));
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
        IFeature feature = m_features.getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(translate("deathbans.not_loaded", locale(request.getSender())));
            return;
        }
        String playername = request.getFirstArg();
        long duration = request.parseDuration(1);
        ((DeathBansFeature)feature).banPlayer(Bukkit.getOfflinePlayer(playername), translate("deathbans.ban_message", locale(request.getSender())), duration);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("name", playername);
        vars.put("time", WordsUtil.formatTimeLeft(System.currentTimeMillis() + duration));
        request.sendMessage(translate("deathbans.banned", locale(request.getSender()), vars));
    }
}
