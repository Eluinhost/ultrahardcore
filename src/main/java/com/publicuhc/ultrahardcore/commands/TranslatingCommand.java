/*
 * Command.java
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

import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.api.Command;
import org.bukkit.command.CommandSender;

import java.util.Locale;

class TranslatingCommand implements Command {

    protected final Translate translate;

    @Inject
    protected TranslatingCommand(Translate translate){
        this.translate = translate;
    }

    /**
     * Proxy method.
     *
     * @see com.publicuhc.pluginframework.translate.Translate#translate(String, Locale, Object...)
     */
    public String translate(String key, Locale locale, Object... params) {
        return translate.translate(key, locale, params);
    }

    /**
     * Proxy method.
     *
     * @see com.publicuhc.pluginframework.translate.Translate#translate(String, CommandSender, Object...)
     */
    public String translate(String key, CommandSender sender, Object... params) {
        return translate.translate(key, sender, params);
    }
}
