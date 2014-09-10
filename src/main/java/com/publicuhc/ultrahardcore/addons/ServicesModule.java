/*
 * ServicesModule.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
