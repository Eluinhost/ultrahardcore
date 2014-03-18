package com.publicuhc.ultrahardcore.commands;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.commands.SenderType;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.util.ServerUtil;

public class HealCommand extends SimpleCommand {

    public static final String HEAL_SELF_PERMISSION = "UHC.heal.self";
    public static final String HEAL_OTHER_PERMISSION = "UHC.heal.other";
    public static final String HEAL_ANNOUNCE_PERMISSION = "UHC.heal.announce";

    @Inject
    private HealCommand(ConfigManager configManager) {
        super(configManager);
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
        player.sendMessage(translate("heal.tell"));
        ServerUtil.broadcastForPermission(translate("heal.announce").replaceAll("%healer%",player.getName()).replaceAll("%name%",player.getName()),HEAL_ANNOUNCE_PERMISSION);
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
            request.sendMessage(translate("heal.invalid_player").replaceAll("%name%",request.getFirstArg()));
            return;
        }
        p.setHealth(p.getMaxHealth());
        p.sendMessage(translate("heal.tell"));
        request.sendMessage(translate("heal.healed"));
        ServerUtil.broadcastForPermission(translate("heal.announce").replaceAll("%healer%",request.getSender().getName()).replaceAll("%name%",p.getName()), HEAL_ANNOUNCE_PERMISSION);
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
            p.sendMessage(translate("heal.tell"));
        }
        request.sendMessage(translate("heal.heal_all"));
        ServerUtil.broadcastForPermission(translate("heal.heal_all_announce").replaceAll("%name%",request.getSender().getName()), HEAL_ANNOUNCE_PERMISSION);
    }
}
