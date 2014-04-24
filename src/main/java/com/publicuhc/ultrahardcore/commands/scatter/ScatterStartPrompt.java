/*
 * ScatterStartPrompt.java
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

import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

/**
 * Project: UHC
 * Package: com.publicuhc.ultrahardcore.commands.scatter
 * Created by Eluinhost on 15:57 29/03/14 2014.
 */
public class ScatterStartPrompt extends MessagePrompt {

    public static final String CONFIG_MANAGER = "command_handler";
    public static final String SCATTER_MANAGER = "scatter_manager";
    public static final String PLUGIN = "plugin_class";

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return new ScatterTypePrompt((ScatterManager) conversationContext.getSessionData(SCATTER_MANAGER));
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Starting interactive scatter. You can end this command at any point by tying 'cancel' or by waiting.";
    }
}
