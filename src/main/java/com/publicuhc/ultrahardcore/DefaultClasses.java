/*
 * DefaultClasses.java
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

import com.publicuhc.ultrahardcore.features.FeatureManager;
import com.publicuhc.ultrahardcore.features.IFeature;
import com.publicuhc.ultrahardcore.features.exceptions.FeatureIDConflictException;
import com.publicuhc.ultrahardcore.features.exceptions.InvalidFeatureIDException;
import com.publicuhc.pluginframework.commands.routing.Router;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Injector;
import com.publicuhc.ultrahardcore.borders.BorderTypeManager;
import com.publicuhc.ultrahardcore.borders.exceptions.BorderIDConflictException;
import com.publicuhc.ultrahardcore.borders.types.CylinderBorder;
import com.publicuhc.ultrahardcore.borders.types.RoofBorder;
import com.publicuhc.ultrahardcore.borders.types.SquareBorder;
import com.publicuhc.ultrahardcore.commands.*;
import com.publicuhc.ultrahardcore.commands.scatter.ScatterCommand;
import com.publicuhc.ultrahardcore.pluginfeatures.anonchat.AnonChatFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.deathbans.DeathBansFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.deathdrops.DeathDropsFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.deathlightning.DeathLightningFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.deathmessages.DeathMessagesFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.enderpearls.EnderpearlsFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.footprints.FootprintFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.ghastdrops.GhastDropsFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.goldenheads.GoldenHeadsFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.hardcorehearts.HardcoreHeartsFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.nether.NetherFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.playerfreeze.PlayerFreezeFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.playerheads.PlayerHeadsFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.playerlist.PlayerListFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.portals.PortalsFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.potionnerfs.PotionNerfsFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.recipes.RecipeFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.regen.RegenFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.timer.TimerFeature;
import com.publicuhc.ultrahardcore.pluginfeatures.witchspawns.WitchSpawnsFeature;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import com.publicuhc.ultrahardcore.scatter.exceptions.ScatterTypeConflictException;
import com.publicuhc.ultrahardcore.scatter.types.AbstractScatterType;
import com.publicuhc.ultrahardcore.scatter.types.EvenCircumferenceType;
import com.publicuhc.ultrahardcore.scatter.types.RandomCircularType;
import com.publicuhc.ultrahardcore.scatter.types.RandomSquareType;

import java.util.logging.Logger;

@SuppressWarnings({"OverlyCoupledClass", "OverlyCoupledMethod"})
public class DefaultClasses {

     private final FeatureManager m_featureManager;
    private final BorderTypeManager m_borderTypes;
    private final Router m_router;
    private final ScatterManager m_scatterManager;
    private final Logger m_logger;

    /**
     * @param featureManager the feature manager
     * @param borders the border manager
     * @param router the command router
     * @param scatterManager the scatter manager
     * @param logger the logger
     */
    @Inject
    public DefaultClasses(FeatureManager featureManager,
                          BorderTypeManager borders, Router router, ScatterManager scatterManager,
                          Logger logger){
        m_logger = logger;
        m_featureManager = featureManager;
        m_borderTypes = borders;
        m_router = router;
        m_scatterManager = scatterManager;
    }

    /**
     * Load the default border types, make sure worldedit exists first
     */
    public void loadBorders() {
        try {
            m_borderTypes.addBorder(new CylinderBorder());
            m_borderTypes.addBorder(new RoofBorder());
            m_borderTypes.addBorder(new SquareBorder());
        } catch (BorderIDConflictException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load all the default commands
     */
    public void loadDefaultCommands() {
        Class[] classes = {
                HealCommand.class,
                ClearInventoryCommand.class,
                TPCommand.class,
                FeatureCommand.class,
                TeamCommands.class,
                FeedCommand.class,
                FreezeCommand.class,
                BorderCommand.class,
                DeathBanCommand.class,
                ScatterCommand.class,
                TimerCommand.class,
                WhitelistCommands.class,
                TeamRequestsCommands.class
        };
        for(Class clazz : classes){
            try {
                m_router.registerCommands(clazz);
            } catch (@SuppressWarnings("OverlyBroadCatchBlock") Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load all the default features into the feature manager
     * @param injector the injector
     */
    @Inject
    public void loadDefaultFeatures(Injector injector) {
        m_logger.info("Loading UHC feature modules...");
        //Load the default features with settings in config
        Class<? extends IFeature>[] classes = new Class[]{
            DeathLightningFeature.class,
            EnderpearlsFeature.class,
            GhastDropsFeature.class,
            PlayerHeadsFeature.class,
            PlayerListFeature.class,
            RecipeFeature.class,
            RegenFeature.class,
            DeathMessagesFeature.class,
            DeathDropsFeature.class,
            AnonChatFeature.class,
            GoldenHeadsFeature.class,
            DeathBansFeature.class,
            PotionNerfsFeature.class,
            NetherFeature.class,
            WitchSpawnsFeature.class,
            PortalsFeature.class,
            PlayerFreezeFeature.class,
            HardcoreHeartsFeature.class,
            FootprintFeature.class,
            TimerFeature.class
        };
        for(Class<? extends IFeature> clazz : classes){
            try{
                IFeature feature = injector.getInstance(clazz);
                m_featureManager.addFeature(feature);
            } catch (FeatureIDConflictException ignored) {
                m_logger.severe("A default UHC Feature ID is conflicting, this should never happen!");
            } catch (InvalidFeatureIDException ignored) {
                m_logger.severe("A default UHC feature ID is invalid, this should never happen!");
            } catch (NoClassDefFoundError ignored) {
                m_logger.severe("Couldn't find protocollib for related feature, skipping...");
            }
        }
    }

    /**
     * Load the default scatter types
     * @param injector the injector
     */
    @Inject
    public void loadDefaultScatterTypes(Injector injector){
        Class<? extends AbstractScatterType>[] types = new Class[]{
                EvenCircumferenceType.class,
                RandomCircularType.class,
                RandomSquareType.class
        };
        for(Class<? extends AbstractScatterType> clazz : types){
            try {
                AbstractScatterType type = injector.getInstance(clazz);
                m_scatterManager.addScatterType(type);
            } catch (ScatterTypeConflictException ignored) {
                m_logger.severe("Conflict error when loading default scatter types!");
            }
        }
    }
}
