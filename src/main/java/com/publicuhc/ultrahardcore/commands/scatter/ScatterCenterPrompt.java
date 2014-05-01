/*
 * ScatterCenterPrompt.java
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

package com.publicuhc.ultrahardcore.commands.scatter;

import com.publicuhc.pluginframework.commands.prompts.XZCoordinatePrompt;
import com.publicuhc.pluginframework.util.SimplePair;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class ScatterCenterPrompt extends XZCoordinatePrompt {

    public static final String CENTER_DATA = "center";

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter center coords: x,z";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, SimplePair<Double, Double> coords) {
        conversationContext.setSessionData(CENTER_DATA,coords);
        return new ScatterRadiusPrompt();
    }
}
