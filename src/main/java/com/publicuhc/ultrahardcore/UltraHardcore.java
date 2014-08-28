/*
 * UltraHardcore.java
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
package com.publicuhc.ultrahardcore;

import com.publicuhc.pluginframework.FrameworkJavaPlugin;
import com.publicuhc.pluginframework.routing.Router;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.shaded.inject.*;
import com.publicuhc.pluginframework.shaded.metrics.Metrics;
import com.publicuhc.ultrahardcore.api.*;
import com.publicuhc.ultrahardcore.api.events.AddonInitializeEvent;
import com.publicuhc.ultrahardcore.api.exceptions.FeatureIDConflictException;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * UltraHardcore
 * <p/>
 * Main plugin class, init
 *
 * @author ghowden
 */
@Singleton
public class UltraHardcore extends FrameworkJavaPlugin {

    private FeatureManager featureManager;
    private Injector mainInjector;
    private Router router;
    private Metrics metrics;

    //When the plugin gets started
    @Override
    protected void onFrameworkEnable()
    {
        //load the core addon
        registerAddon(new UHCCoreAddonModule());

        //enable metrics
        Metrics.Graph graph = metrics.createGraph("Features Loaded");
        for(final Feature feature : featureManager.getFeatures()){
            graph.addPlotter(new Metrics.Plotter(feature.getFeatureID()) {
                @Override
                public int getValue() {
                    return feature.isEnabled() ? 1 : 0;
                }
            });
        }
        metrics.addGraph(graph);
        metrics.start();
    }

    @Inject
    private void setMainInjector(Injector injector)
    {
        mainInjector = injector;
    }

    @Inject
    private void setFeatureManager(FeatureManager featureManager)
    {
        this.featureManager = featureManager;
    }

    @Inject
    private void setRouter(Router router)
    {
        this.router = router;
    }

    @Inject
    private void setMetrics(Metrics metrics)
    {
        this.metrics = metrics;
    }

    /**
     * @return the feature manager for handling features
     */
    public FeatureManager getFeatureManager()
    {
        return featureManager;
    }

    /**
     * @return the plugin router, used for registering commands e.t.c.
     */
    public Router getRouter()
    {
        return router;
    }

    @Override
    protected void initialModules(List<Module> modules)
    {
        modules.addAll(getDefaultModules());
        modules.add(new UHCModule());
    }

    /**
     * Register an addon module for features/commands
     *
     * @param module the module with the bindings within it
     */
    @SuppressWarnings({"AnonymousInnerClass", "EmptyClass"})
    public void registerAddon(UHCAddonModule module) {
        AddonInitializeEvent event = new AddonInitializeEvent(module);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            getLogger().log(Level.INFO, "Initializing addon from module class " + module.getClass().getName() + " was cancelled");
            return;
        }

        //fetch the list of UHCFeatures from the module and add them to the manager
        Injector injector = mainInjector.createChildInjector(module);

        Set<UHCFeature> features = injector.getInstance(Key.get(new TypeLiteral<Set<UHCFeature>>(){}));
        for(UHCFeature feature : features) {
            try {
                featureManager.addFeature(feature);
            } catch (FeatureIDConflictException e) {
                e.printStackTrace();
            }
        }

        Set<Command> commands = injector.getInstance(Key.get(new TypeLiteral<Set<Command>>(){}));
        for(Command command : commands) {
            try {
                getRouter().registerCommands(command, false);
            } catch (CommandParseException e) {
                e.printStackTrace();
            }
        }
    }
}