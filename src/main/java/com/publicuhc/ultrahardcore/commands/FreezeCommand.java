package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import org.bukkit.entity.Player;
import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.features.FeatureManager;
import com.publicuhc.features.IFeature;
import com.publicuhc.ultrahardcore.features.playerfreeze.PlayerFreezeFeature;

public class FreezeCommand extends SimpleCommand {

    public static final String FREEZE_PERMISSION = "UHC.freeze.command";
    public static final String ANTIFREEZE_PERMISSION = "UHC.freeze.antifreeze";

    private final FeatureManager m_features;

    /**
     * The freeze command
     * @param features the feature manager
     * @param configManager the config manager
     */
    @Inject
    private FreezeCommand(FeatureManager features, ConfigManager configManager) {
        super(configManager);
        m_features = features;
    }

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
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded"));
            return;
        }
        Player player = request.getPlayer(0);
        if(player == null){
            request.sendMessage(translate("freeze.invalid_player").replaceAll("%name%",request.getFirstArg()));
            return;
        }
        if(player.hasPermission(ANTIFREEZE_PERMISSION)){
            request.sendMessage(translate("freeze.immune"));
            return;
        }
        ((PlayerFreezeFeature)feature).addPlayer(player);
        request.sendMessage(translate("freeze.player_froze"));
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
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded"));
            return;
        }
        ((PlayerFreezeFeature)feature).freezeAll();
        request.sendMessage(translate("freeze.froze_all"));
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
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded"));
            return;
        }
        ((PlayerFreezeFeature)feature).removePlayer(request.getFirstArg());
        request.sendMessage(translate("freeze.player_unfroze"));
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
        IFeature feature = m_features.getFeatureByID("PlayerFreeze");
        if(feature == null){
            request.sendMessage(translate("freeze.not_loaded"));
            return;
        }
        ((PlayerFreezeFeature)feature).unfreezeAll();
        request.sendMessage(translate("freeze.unfroze_all"));
    }
}
