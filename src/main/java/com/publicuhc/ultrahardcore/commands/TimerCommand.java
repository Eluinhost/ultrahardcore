package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.timer.TimerFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.timer.TimerRunnable;

import java.util.List;

public class TimerCommand extends SimpleCommand {

    public static final String TIMER_COMMAND = "UHC.timer";

    private final FeatureManager m_featureManager;

    /**
     * @param configManager the config manager
     * @param translate the translator
     * @param featureManager the feature manager
     */
    @Inject
    private TimerCommand(Configurator configManager, Translate translate, FeatureManager featureManager) {
        super(configManager, translate);
        m_featureManager = featureManager;
    }

    /**
     * Ran on /timer {duration} {message}*
     * @param request the request params
     */
    @Command(
            identifier = "TimerCommand",
            trigger = "timer",
            minArgs = 2,
            permission = TIMER_COMMAND
    )
    public void onTimerCommand(CommandRequest request) {
        IFeature feature = m_featureManager.getFeatureByID("Timer");
        if(feature == null) {
            request.sendMessage(translate("timer.feature_not_found", locale(request.getSender())));
            return;
        }
        TimerFeature timerFeature = (TimerFeature) feature;
        if(!feature.isEnabled()){
            request.sendMessage(translate("timer.not_enabled", locale(request.getSender())));
            return;
        }
        if(!request.isArgInt(0)) {
            request.sendMessage(translate("timer.invalid_time", locale(request.getSender())));
            return;
        }

        int seconds = request.getInt(0);

        List<String> args = request.getArgs();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.size(); i++) {
            sb.append(args.get(i)).append(" ");
        }
        String message = sb.toString();

        if(timerFeature.startTimer(TimerRunnable.TICKS_PER_SECOND*seconds,message)) {
            request.sendMessage(translate("timer.running", locale(request.getSender())));
        }else {
            request.sendMessage(translate("timer.already_running", locale(request.getSender())));
        }
    }

    /**
     * Ran on /canceltimer
     * @param request the request params
     */
    @Command(
            identifier = "TimerCancelCommand",
            trigger = "canceltimer",
            maxArgs = 0,
            minArgs = 0,
            permission = TIMER_COMMAND
    )
    public void onTimerCancelCommand(CommandRequest request) {
        IFeature feature = m_featureManager.getFeatureByID("Timer");
        if(feature == null) {
            request.sendMessage(translate("timer.feature_not_found", locale(request.getSender())));
            return;
        }
        TimerFeature timerFeature = (TimerFeature) feature;
        timerFeature.stopTimer();
        request.sendMessage(translate("timer.cancelled", locale(request.getSender())));
    }
}
