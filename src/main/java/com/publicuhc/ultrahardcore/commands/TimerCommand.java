/*
 * TimerCommand.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.routing.RouteBuilder;
import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.timer.TimerFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.timer.TimerRunnable;
import com.publicuhc.ultrahardcore.util.WordsUtil;

import java.util.List;
import java.util.regex.Pattern;

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
    @CommandMethod
    public void timerCommand(CommandRequest request) {
        IFeature feature = m_featureManager.getFeatureByID("Timer");
        if(feature == null) {
            request.sendMessage(translate("timer.feature_not_found", request.getLocale()));
            return;
        }
        TimerFeature timerFeature = (TimerFeature) feature;
        if(!feature.isEnabled()){
            request.sendMessage(translate("timer.not_enabled", request.getLocale()));
            return;
        }
        long millis = WordsUtil.parseTime(request.getFirstArg());

        int seconds = (int) (millis / 1000);

        List<String> args = request.getArgs();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.size(); i++) {
            sb.append(args.get(i)).append(" ");
        }
        String message = sb.toString();

        if(timerFeature.startTimer(TimerRunnable.TICKS_PER_SECOND*seconds,message)) {
            request.sendMessage(translate("timer.running", request.getLocale()));
        }else {
            request.sendMessage(translate("timer.already_running", request.getLocale()));
        }
    }

    @RouteInfo
    public void timerCommandDetails(RouteBuilder builder) {
        builder.restrictCommand("timer");
        builder.restrictPattern(Pattern.compile("[\\S]+ [\\S]+.*"));
        builder.restrictPermission(TIMER_COMMAND);
    }
    /**
     * Ran on /canceltimer
     * @param request the request params
     */
    @CommandMethod
    public void timerCancelCommand(CommandRequest request) {
        IFeature feature = m_featureManager.getFeatureByID("Timer");
        if(feature == null) {
            request.sendMessage(translate("timer.feature_not_found", request.getLocale()));
            return;
        }
        TimerFeature timerFeature = (TimerFeature) feature;
        timerFeature.stopTimer();
        request.sendMessage(translate("timer.cancelled", request.getLocale()));
    }

    @RouteInfo
    public void timerCancelCommand(RouteBuilder builder) {
        builder.restrictCommand("canceltimer");
        builder.restrictPermission(TIMER_COMMAND);
    }
}
