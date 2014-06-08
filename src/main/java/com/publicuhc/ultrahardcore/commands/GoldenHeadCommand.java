package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.goldenheads.GoldenHeadsFeature;

public class GoldenHeadCommand extends SimpleCommand {

    public static final String HEAD_COMMAND_PERMISSION = "UHC.goldenhead.command";

    private final FeatureManager m_featureManager;

    /**
     * @param configManager the config manager
     * @param translate     the translator
     */
    @Inject
    protected GoldenHeadCommand(Configurator configManager, Translate translate, FeatureManager featureManager) {
        super(configManager, translate);
        m_featureManager = featureManager;
    }

    @CommandMethod
    public void changeHeadHealingAmountCommand(CommandRequest request) {
        if(!request.isArgInt(0)) {
            request.sendMessage(translate("headheal.not_int", request.getLocale()));
            return;
        }
        int amount = request.getInt(0);

        IFeature feature = m_featureManager.getFeatureByID("GoldenHeads");
        if(!(feature instanceof GoldenHeadsFeature)) {
            request.sendMessage(translate("headheal.feature_not_found", request.getLocale()));
            return;
        }
        GoldenHeadsFeature gheadFeature = (GoldenHeadsFeature) feature;
        gheadFeature.setAmountExtra(amount);

        request.sendMessage(translate("headheal.updated", request.getLocale()));
    }

    @RouteInfo
    public void changeHeadHealingAmountCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(HEAD_COMMAND_PERMISSION)
                .restrictArgumentCount(1, -1)
                .restrictCommand("headheal");
    }
}
