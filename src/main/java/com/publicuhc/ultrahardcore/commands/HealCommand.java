package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.commands.SenderType;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HealCommand extends SimpleCommand {

    public static final String HEAL_SELF_PERMISSION = "UHC.heal.self";
    public static final String HEAL_OTHER_PERMISSION = "UHC.heal.other";
    public static final String HEAL_ANNOUNCE_PERMISSION = "UHC.heal.announce";

    @Inject
    private HealCommand(Configurator configManager, Translate translate) {
        super(configManager, translate);
    }

    /**
     * Ran on /healself
     * @param request request params
     */
    @Command(trigger = "healself",
            identifier = "HealSelfCommand",
            minArgs = 0,
            maxArgs = 0,
            senders = {SenderType.PLAYER},
            permission = HEAL_SELF_PERMISSION)
    public void onHealSelfCommand(CommandRequest request){
        Player player = (Player) request.getSender();
        player.setHealth(player.getMaxHealth());
        player.sendMessage(translate("heal.tell", locale(request.getSender())));
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("healer", player.getName());
        vars.put("name", player.getName());
        ServerUtil.broadcastForPermission(translate("heal.announce", locale(request.getSender()), vars),HEAL_ANNOUNCE_PERMISSION);
    }

    /**
     * Ran on /heal {name}
     * @param request request params
     */
    @Command(trigger = "heal",
            identifier = "HealCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = HEAL_OTHER_PERMISSION)
    public void onHealCommand(CommandRequest request){
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
     * Ran on /heal *
     * @param request request params
     */
    @Command(trigger = "*",
            identifier = "HealAllCommand",
            parentID = "HealCommand",
            permission = HEAL_OTHER_PERMISSION,
            minArgs = 0,
            maxArgs = 0)
    public void onHealAllCommand(CommandRequest request){
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(p.getMaxHealth());
            p.sendMessage(translate("heal.tell", locale(request.getSender())));
        }
        request.sendMessage(translate("heal.heal_all", locale(request.getSender())));
        ServerUtil.broadcastForPermission(translate("heal.heal_all_announce", locale(request.getSender()), "name", request.getSender().getName()), HEAL_ANNOUNCE_PERMISSION);
    }
}
