package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.routing.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.deathbans.DeathBansFeature;
import com.publicuhc.ultrahardcore.util.WordsUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
    @CommandMethod
    public void deathBanCommand(CommandRequest request){
        request.sendMessage(ChatColor.RED+"Syntax: /deathban unban <name>");
        request.sendMessage(ChatColor.RED+"Syntax: /deathban ban <name> <time>");
    }

    /**
     * Run on /deathban.*
     * @param builder the builder
     */
    @RouteInfo
    public void deathBanCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("deathban");
        builder.restrictPermission(DEATH_BAN_BAN);
        builder.maxMatches(1);
    }

    /**
     * Unban a player
     * @param request the request params
     */
    @CommandMethod
    public void deathbanUnbanCommand(CommandRequest request){
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
     * Ran on /deathban unban {player}
     * @param builder the builder
     */
    @RouteInfo
    public void deathbanUnbanCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(DEATH_BAN_UNBAN);
        builder.restrictCommand("deathban");
        builder.restrictPattern(Pattern.compile("unban .+"));
    }

    /**
     * ban a player
     * @param request the request params
     */
    public void onDeathBanBanCommand(CommandRequest request){
        IFeature feature = m_features.getFeatureByID("DeathBans");
        if(feature == null){
            request.sendMessage(translate("deathbans.not_loaded", locale(request.getSender())));
            return;
        }
        String playername = request.getFirstArg();
        long duration = WordsUtil.parseTime(request.getArg(1));
        ((DeathBansFeature)feature).banPlayer(Bukkit.getOfflinePlayer(playername), translate("deathbans.ban_message", locale(request.getSender())), duration);
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("name", playername);
        vars.put("time", WordsUtil.formatTimeLeft(System.currentTimeMillis() + duration));
        request.sendMessage(translate("deathbans.banned", locale(request.getSender()), vars));
    }

    /**
     * Ran on /deathban ban {player} {time}
     * @param builder the builder
     */
    @RouteInfo
    public void deathbanBanCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("deathban");
        builder.restrictPermission(DEATH_BAN_BAN);
        builder.restrictPattern(Pattern.compile("ban .+ .+"));
    }
}
