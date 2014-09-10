/*
 * ServicesModule.java
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

package com.publicuhc.ultrahardcore.addons;

import com.publicuhc.pluginframework.PluginModule;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.multibindings.Multibinder;
import com.publicuhc.pluginframework.translate.TranslateModule;
import com.publicuhc.ultrahardcore.api.Command;
import com.publicuhc.ultrahardcore.api.UHCAddonConfiguration;
import com.publicuhc.ultrahardcore.api.UHCFeature;
import org.bukkit.plugin.Plugin;

/**
 * All of the services that are individual for each addon
 */
public class ServicesModule extends AbstractModule
{

    private final Plugin instance;
    private final UHCAddonConfiguration configuration;

    public ServicesModule(Plugin instance, UHCAddonConfiguration configuration)
    {
        this.instance = instance;
        this.configuration = configuration;
    }

    @Override
    protected void configure()
    {
        //bind the things
        configuration.configureCommands(Multibinder.newSetBinder(binder(), Command.class));
        configuration.configureFeatures(Multibinder.newSetBinder(binder(), UHCFeature.class));

        //translate per plugin, locales bound in shared services
        install(new TranslateModule());

        //plugin module separate for the modules that use it and for logger injection
        install(new PluginModule(instance));

        //configuration per addon plugin
        install(new ConfigurationModule(instance.getClass().getClassLoader()));
    }
}
