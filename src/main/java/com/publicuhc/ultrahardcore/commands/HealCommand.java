package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.matchers.AnyRouteMatcher;
import com.publicuhc.pluginframework.commands.matchers.PatternRouteMatcher;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routing.DefaultMethodRoute;
import com.publicuhc.pluginframework.commands.routing.MethodRoute;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HealCommand extends SimpleCommand {

    public static final String HEAL_SELF_PERMISSION = "UHC.heal.self";
    public static final String HEAL_OTHER_PERMISSION = "UHC.heal.other";
    public static final String HEAL_ANNOUNCE_PERMISSION = "UHC.heal.announce";

    @Inject
    private HealCommand(Configurator configManager, Translate translate) {
        super(configManager, translate);
    }

    /**
     * heal yourself
     * @param request request params
     */
    @CommandMethod
    public void healSelfCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        player.setHealth(player.getMaxHealth());
        player.sendMessage(translate("heal.tell", locale(request.getSender())));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("healer", player.getName());
        vars.put("name", player.getName());
        ServerUtil.broadcastForPermission(translate("heal.announce", locale(request.getSender()), vars),HEAL_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /healself.*
     * @return the route
     */
    @RouteInfo
    public MethodRoute healSelfCommandDetails() {
        return new DefaultMethodRoute(
                new AnyRouteMatcher(),
                new SenderType[] {
                        SenderType.PLAYER
                },
                HEAL_SELF_PERMISSION,
                "healself"
        );
    }

    /**
     * heal someone else
     * @param request request params
     */
    @CommandMethod
    public void healCommand(CommandRequest request){
        Player p = Bukkit.getPlayer(request.getFirstArg());
        if (p == null) {
            request.sendMessage(translate("heal.invalid_player", locale(request.getSender()), "name", request.getFirstArg()));
            return;
        }
        p.setHealth(p.getMaxHealth());
        p.sendMessage(translate("heal.tell", locale(request.getSender())));
        request.sendMessage(translate("heal.healed", locale(request.getSender())));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("healer", request.getSender().getName());
        vars.put("name", p.getName());
        ServerUtil.broadcastForPermission(translate("heal.announce", locale(request.getSender()), vars), HEAL_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /heal .+ except '/heal *'
     * @return the route
     */
    @RouteInfo
    public MethodRoute healCommandDetails() {
        return new DefaultMethodRoute(
                new PatternRouteMatcher(Pattern.compile("[^*]+")),
                new SenderType[] {
                        SenderType.PLAYER,
                        SenderType.CONSOLE,
                        SenderType.COMMAND_BLOCK,
                        SenderType.REMOTE_CONSOLE
                },
                HEAL_OTHER_PERMISSION,
                "heal"
        );
    }

    /**
     * Ran on /heal *
     * @param request request params
     */
    @CommandMethod
    public void healAllCommand(CommandRequest request){
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(p.getMaxHealth());
            p.sendMessage(translate("heal.tell", locale(request.getSender())));
        }
        request.sendMessage(translate("heal.heal_all", locale(request.getSender())));
        ServerUtil.broadcastForPermission(translate("heal.heal_all_announce", locale(request.getSender()), "name", request.getSender().getName()), HEAL_ANNOUNCE_PERMISSION);
    }

    /**
     * Run on /heal * only
     * @return the route
     */
    @RouteInfo
    public MethodRoute healAllCommandDetails() {
        return new DefaultMethodRoute(
                new PatternRouteMatcher(Pattern.compile("\\*")),
                new SenderType[] {
                        SenderType.PLAYER,
                        SenderType.CONSOLE,
                        SenderType.COMMAND_BLOCK,
                        SenderType.REMOTE_CONSOLE
                },
                HEAL_OTHER_PERMISSION,
                "heal"
        );
    }
}
