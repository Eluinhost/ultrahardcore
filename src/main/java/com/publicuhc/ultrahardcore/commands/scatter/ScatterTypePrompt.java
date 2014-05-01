/*
 * ScatterTypePrompt.java
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

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;

public class ScatterTypePrompt extends FixedSetPrompt {

    public static final String TYPE_DATA = "scatter_type";

    /**
     * Allows any of the loaded scatter type names
     * @param manager the scatter manager
     */
    public ScatterTypePrompt(ScatterManager manager){
        super(manager.getScatterTypeNames());
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        ScatterManager scatterManager = (ScatterManager) conversationContext.getSessionData(ScatterStartPrompt.SCATTER_MANAGER);
        conversationContext.setSessionData(TYPE_DATA,scatterManager.getScatterType(s));
        return new ScatterWorldPrompt();
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter type of scatter to use: "+formatFixedSet();
    }
}
