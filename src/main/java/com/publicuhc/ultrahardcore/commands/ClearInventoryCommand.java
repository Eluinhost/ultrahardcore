package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.matchers.AnyRouteMatcher;
import com.publicuhc.pluginframework.commands.matchers.PatternRouteMatcher;
import com.publicuhc.pluginframework.commands.matchers.SimpleRouteMatcher;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routing.DefaultMethodRoute;
import com.publicuhc.pluginframework.commands.routing.MethodRoute;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ClearInventoryCommand extends SimpleCommand {

    public static final String CLEAR_SELF_PERMISSION = "UHC.ci.self";
    public static final String CLEAR_OTHER_PERMISSION = "UHC.ci.other";
    public static final String CLEAR_IMMUNE_PERMISSION = "UHC.ci.immune";

    /**
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private ClearInventoryCommand(Configurator configManager, Translate translate) {
        super(configManager, translate);
    }

    /**
     * Ran on /ciself
     * @param request request params
     */
    @CommandMethod
    public void clearInventorySelf(CommandRequest request){
        clearInventory((HumanEntity) request.getSender());
        request.sendMessage(translate("ci.cleared", locale(request.getSender())));
    }

    /**
     * Run on /ciself
     * @return the route
     */
    @RouteInfo
    public MethodRoute clearInventorySelfDetails() {
        return new DefaultMethodRoute(
                new AnyRouteMatcher(),
                new SenderType[] {
                        SenderType.PLAYER
                },
                CLEAR_SELF_PERMISSION,
                "ciself"
        );
    }

    /**
     * Ran on /ci {name}*
     * @param request request params
     */
    @CommandMethod
    public void clearInventoryCommand(CommandRequest request){
        List<String> arguments = request.getArgs();
        AbstractList<String> namesNotFound = new ArrayList<String>();
        for (String pname : arguments) {
            Player p = Bukkit.getPlayer(pname);
            if (p == null) {
                namesNotFound.add(pname);
                continue;
            }
            if (p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                request.sendMessage(translate("ci.immune", locale(request.getSender()), "name", p.getName()));
            } else {
                clearInventory(p);
                p.sendMessage(translate("ci.tell", locale(request.getSender()), "name", request.getSender().getName()));
            }
        }
        request.sendMessage("ci.cleared");
        if (!namesNotFound.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String s : namesNotFound) {
                message.append(' ').append(s);
            }
            request.sendMessage(translate("ci.not_found", locale(request.getSender()), "list", message.toString()));
        }
    }

    /**
     * Run on /ci .* except /ci *
     * @return the route
     */
    @RouteInfo
    public MethodRoute clearInventoryCommandDetails() {
        return new DefaultMethodRoute(
                new PatternRouteMatcher(Pattern.compile("[^*]+")),
                new SenderType[] {
                        SenderType.PLAYER,
                        SenderType.CONSOLE,
                        SenderType.COMMAND_BLOCK,
                        SenderType.REMOTE_CONSOLE
                },
                CLEAR_OTHER_PERMISSION,
                "ci"
        );
    }

    /**
     * Ran on /ci *
     * @param request request params
     */
    @CommandMethod
    public void clearInventoryAll(CommandRequest request){
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(CLEAR_IMMUNE_PERMISSION)) {
                clearInventory(p);
            }
        }
        Bukkit.broadcastMessage(translate("ci.announce_all", locale(request.getSender()), "name", request.getSender().getName()));
    }

    /**
     * Run on /ci *
     * @return the route
     */
    @RouteInfo
    public MethodRoute clearInventoryAll() {
        return new DefaultMethodRoute(
                new SimpleRouteMatcher("*"),
                new SenderType[] {
                        SenderType.PLAYER,
                        SenderType.CONSOLE,
                        SenderType.COMMAND_BLOCK,
                        SenderType.REMOTE_CONSOLE
                },
                CLEAR_OTHER_PERMISSION,
                "ci"
        );
    }


    /**
     * Clears the player's inventory, armour slots, item on cursor and crafting slots
     * @param p player
     */
    private static void clearInventory(HumanEntity p) {
        p.getInventory().clear();
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.setItemOnCursor(new ItemStack(Material.AIR));
        InventoryView openInventory = p.getOpenInventory();
        if (openInventory.getType() == InventoryType.CRAFTING) {
            openInventory.getTopInventory().clear();
        }
    }
}
