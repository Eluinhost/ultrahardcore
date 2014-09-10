/*
 * DebugCommands.java
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

package com.publicuhc.ultrahardcore.core.commands;

import com.publicuhc.pluginframework.locale.LocaleProvider;
import com.publicuhc.pluginframework.routing.annotation.CommandMethod;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.joptsimple.OptionSet;
import com.publicuhc.ultrahardcore.api.Command;
import org.bukkit.command.CommandSender;

public class DebugCommands implements Command
{

    private final LocaleProvider provider;

    @Inject
    public DebugCommands(LocaleProvider provider)
    {
        this.provider = provider;
    }

    @CommandMethod("locale")
    public void checkLocale(OptionSet set, CommandSender sender)
    {
        sender.sendMessage(provider.localeForCommandSender(sender).getDisplayName());
    }
}
