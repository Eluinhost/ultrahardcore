package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.features.FeatureManager;
import uk.co.eluinhost.features.IFeature;
import uk.co.eluinhost.ultrahardcore.features.playerfreeze.PlayerFreezeFeature;

public class FreezeCommand {

    public static final String FREEZE_PERMISSION = "UHC.freeze.command";
    public static final String ANTIFREEZE_PERMISSION = "UHC.freeze.antifreeze";

    /**
     * Ran on /freeze
     * @param request the request params
     */
    @Command(trigger = "freeze",
            identifier = "FreezeCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = FREEZE_PERMISSION)
    public void onFreezeCommand(CommandRequest request){
        IFeature feature = FeatureManager.getInstance().getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(ChatColor.RED+"The freeze feature is not loaded!");
            return;
        }
        Player player = request.getPlayer(0);
        if(player == null){
            request.sendMessage(ChatColor.RED+"Invalid player name "+request.getFirstArg());
            return;
        }
        if(player.hasPermission(ANTIFREEZE_PERMISSION)){
            request.sendMessage(ChatColor.RED+"Player is immune to freezing");
            return;
        }
        ((PlayerFreezeFeature)feature).addPlayer(player.getName());
        request.sendMessage(ChatColor.GOLD+"Player frozen");
    }

    /**
     * Ran on /freeze *
     * @param request the request params
     */
    @Command(trigger = "*",
            identifier = "FreezeAllCommand",
            minArgs = 0,
            maxArgs = 0,
            parentID = "FreezeCommand",
            permission = FREEZE_PERMISSION)
    public void onFreezeAllCommand(CommandRequest request){
        IFeature feature = FeatureManager.getInstance().getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(ChatColor.RED+"The freeze feature is not loaded!");
            return;
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            ((PlayerFreezeFeature)feature).addPlayer(player.getName());
        }
        request.sendMessage(ChatColor.GOLD+"Froze all online players");
    }

    /**
     * Ran on /unfreeze
     * @param request the request params
     */
    @Command(trigger = "unfreeze",
            identifier = "UnfreezeCommand",
            minArgs = 1,
            maxArgs = 1,
            permission = FREEZE_PERMISSION)
    public void onUnfreezeCommand(CommandRequest request){
        IFeature feature = FeatureManager.getInstance().getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(ChatColor.RED+"The freeze feature is not loaded!");
            return;
        }
        ((PlayerFreezeFeature)feature).removePlayer(request.getFirstArg());
        request.sendMessage(ChatColor.GOLD+"Player unfrozen");
    }

    /**
     * Ran on /unfreeze *
     * @param request the request params
     */
    @Command(trigger = "*",
            identifier = "UnfreezeAllCommand",
            parentID = "UnfreezeCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = FREEZE_PERMISSION)
    public void onUnfreezeAllCommand(CommandRequest request){
        IFeature feature = FeatureManager.getInstance().getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(ChatColor.RED+"The freeze feature is not loaded!");
            return;
        }
        ((PlayerFreezeFeature)feature).unfreezeAll();
        request.sendMessage(ChatColor.GOLD+"Unfroze all players");
    }
}
