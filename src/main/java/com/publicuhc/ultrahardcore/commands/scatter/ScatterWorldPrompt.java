/*
 * ScatterWorldPrompt.java
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

import com.publicuhc.pluginframework.commands.prompts.WorldNamePrompt;
import com.publicuhc.ultrahardcore.util.ServerUtil;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

public class ScatterWorldPrompt extends WorldNamePrompt {

    public static final String WORLD_DATA = "world";

    /**
     * Allow any loaded world
     */
    public ScatterWorldPrompt(){
        super(ServerUtil.getWorldNames());
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter the name of the world to scatter into: "+formatFixedSet();
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, World world) {
        conversationContext.setSessionData(WORLD_DATA,world);
        return new ScatterCenterPrompt();
    }
}
