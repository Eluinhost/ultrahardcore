/*
 * SharedServicesModule.java
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

import com.publicuhc.pluginframework.locale.LocaleProvider;
import com.publicuhc.pluginframework.routing.Router;
import com.publicuhc.pluginframework.shaded.inject.AbstractModule;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Provides;

/**
 * All the services shared between all of the addons, generally all created by the UHC injector for use in the addon
 * injectors
 */
public class SharedServicesModule extends AbstractModule
{
    private final LocaleProvider provider;
    private final Router router;
    private final FeatureManager manager;

    @Inject
    public SharedServicesModule(LocaleProvider provider, Router router, FeatureManager manager)
    {
        this.provider = provider;
        this.router = router;
        this.manager = manager;
    }

    @Provides
    public LocaleProvider getLocales()
    {
        return provider;
    }

    @Provides
    public Router getRouter()
    {
        return router;
    }

    @Provides
    public FeatureManager getFeatureManager()
    {
        return manager;
    }

    @Override
    protected void configure()
    {}
}
