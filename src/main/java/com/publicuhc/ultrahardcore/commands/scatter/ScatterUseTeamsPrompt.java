/*
 * ScatterUseTeamsPrompt.java
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

import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class ScatterUseTeamsPrompt extends BooleanPrompt{

    public static final String TEAMS_DATA = "teams";

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, boolean bool) {
        conversationContext.setSessionData(TEAMS_DATA, bool);
        return new ScatterPlayerPrompt();
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Scatter as teams?";
    }
}
