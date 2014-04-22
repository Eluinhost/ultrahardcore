package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.playerfreeze.PlayerFreezeFeature;
import org.bukkit.entity.Player;

public class FreezeCommand extends SimpleCommand {

    public static final String FREEZE_PERMISSION = "UHC.freeze.command";
    public static final String ANTIFREEZE_PERMISSION = "UHC.freeze.antifreeze";

    private final FeatureManager m_features;

    /**
     * The freeze command
     * @param features the feature manager
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    private FreezeCommand(FeatureManager features, Configurator configManager, Translate translate) {
        super(configManager, translate);
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
            request.sendMessage(translate("freeze.not_loaded", locale(request.getSender())));
            return;
        }
        Player player = request.getPlayer(0);
        if(player == null){
            request.sendMessage(translate("freeze.invalid_player", locale(request.getSender()), "name", request.getFirstArg()));
            return;
        }
        if(player.hasPermission(ANTIFREEZE_PERMISSION)){
            request.sendMessage(translate("freeze.immune", locale(request.getSender())));
            return;
        }
        ((PlayerFreezeFeature)feature).addPlayer(player);
        request.sendMessage(translate("freeze.player_froze", locale(request.getSender())));
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
            request.sendMessage(translate("freeze.not_loaded", locale(request.getSender())));
            return;
        }
        ((PlayerFreezeFeature)feature).freezeAll();
        request.sendMessage(translate("freeze.froze_all", locale(request.getSender())));
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
            request.sendMessage(translate("freeze.not_loaded", locale(request.getSender())));
            return;
        }
        ((PlayerFreezeFeature)feature).removePlayer(request.getFirstArg());
        request.sendMessage(translate("freeze.player_unfroze", locale(request.getSender())));
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
            request.sendMessage(translate("freeze.not_loaded", locale(request.getSender())));
            return;
        }
        ((PlayerFreezeFeature)feature).unfreezeAll();
        request.sendMessage(translate("freeze.unfroze_all", locale(request.getSender())));
    }
}
