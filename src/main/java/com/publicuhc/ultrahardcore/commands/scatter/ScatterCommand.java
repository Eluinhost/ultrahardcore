/*
 * ScatterCommand.java
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

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.commands.SimpleCommand;
import com.publicuhc.ultrahardcore.scatter.Parameters;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import com.publicuhc.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;
import com.publicuhc.ultrahardcore.scatter.types.AbstractScatterType;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ScatterCommand extends SimpleCommand {

    public static final String SCATTER_COMMAND = "UHC.scatter";
    private static final int CONVERSATION_TIMEOUT = 20;

    private final ConversationFactory m_conversationFactory;

    private final ScatterManager m_scatterManager;

    /**
     * @param configManager the config manager
     * @param translate the translator
     * @param scatterManager the scatter manager
     * @param plugin the plugin to reference
     */
    @Inject
    private ScatterCommand(Configurator configManager, Translate translate, ScatterManager scatterManager, Plugin plugin) {
        super(configManager, translate);
        m_scatterManager = scatterManager;
        Map<Object,Object> init = new HashMap<Object, Object>();
        init.put(ScatterStartPrompt.SCATTER_MANAGER,scatterManager);
        init.put(ScatterStartPrompt.CONFIG_MANAGER,configManager);
        init.put(ScatterStartPrompt.PLUGIN, plugin);
        m_conversationFactory = new ConversationFactory(plugin)
                .withEscapeSequence("cancel")
                .withPrefix(new ScatterPrefix())
                .withTimeout(CONVERSATION_TIMEOUT)
                .withLocalEcho(false)
                .withFirstPrompt(new ScatterStartPrompt())
                .withModality(false)
                .addConversationAbandonedListener(new ScatterAbandonListener())
                .withInitialSessionData(init);
    }


    /**
     * Interactive scatter
     * @param request the request params
     */
    @CommandMethod
    public void interactiveScatterCommand(CommandRequest request){
        m_conversationFactory.buildConversation((Conversable) request.getSender()).begin();
    }

    /**
     * Run on /iscatter.*
     * @param builder the builder
     */
    @RouteInfo
    public void interactiveScatterCommandDetails(RouteBuilder builder) {
        builder.restrictSenderType(SenderType.CONSOLE, SenderType.PLAYER, SenderType.REMOTE_CONSOLE)
                .restrictPermission(SCATTER_COMMAND)
                .restrictCommand("iscatter");
    }

    /**
     *
     * @param request the request params
     */
    @CommandMethod
    public void scatterCommand(CommandRequest request){
        if(m_scatterManager.isScatterInProgress()){
            request.sendMessage(ChatColor.RED+"There is already a scatter in progress, please wait and try again");
            return;
        }

        AbstractScatterType scatterType = m_scatterManager.getScatterType(request.getArg(0));
        if(scatterType == null){
            request.sendMessage(ChatColor.RED+"Invalid scatter type, use /scatter types to see a list of types");
            return;
        }
        World world = request.getWorld(1);
        if(world == null){
            request.sendMessage(ChatColor.RED+"Invalid world supplied");
            return;
        }
        String coordString = request.getArg(2);
        String[] parts = coordString.split(",");
        if(parts.length != 2){
            request.sendMessage(ChatColor.RED+"Invalid coordinates supplied");
            return;
        }
        double x,z;
        try{
            x = Double.valueOf(parts[0]);
            z = Double.valueOf(parts[1]);
        }catch(NumberFormatException ignored){
            request.sendMessage(ChatColor.RED+"Invalid coordinates supplied");
            return;
        }
        if(!request.isArgNumber(3)){
            request.sendMessage(ChatColor.RED+"Invalid radius supplied");
            return;
        }
        double radius = request.getNumber(3).doubleValue();
        if(!request.isArgNumber(4)){
            request.sendMessage(ChatColor.RED+"Invalid min distance supplied");
            return;
        }
        double minDistance = request.getNumber(4).doubleValue();
        if(!request.isArgBoolean(5)){
            request.sendMessage(ChatColor.RED+"Invalid value supplied for use teams true/false");
            return;
        }
        boolean asTeam = request.getBoolean(5);
        Collection<Player> scatterPlayers = new LinkedList<Player>();

        for(int i = 6; i < request.getArgs().size(); i++){
            if("*".equals(request.getArg(i))){
                scatterPlayers.addAll(Bukkit.getOnlinePlayers());
                continue;
            }
            Player p = request.getPlayer(i);
            if(p == null){
                request.sendMessage(ChatColor.RED+"Player "+request.getArg(i)+" not found");
                continue;
            }
            scatterPlayers.add(p);
        }

        if(scatterPlayers.isEmpty()){
            request.sendMessage(ChatColor.RED+"No players found to scatter");
            return;
        }

        Parameters params = new Parameters(new Location(world,x,0,z));
        params.setRadius(radius);
        params.setMinimumDistance(minDistance);
        params.setAsTeam(asTeam);

        FileConfiguration config = getConfigurator().getConfig("main");
        List<String> materials = config.getStringList("scatter.allowedBlocks");
        Collection<Material> mats = new ArrayList<Material>();
        for(String s : materials){
            Material mat = Material.matchMaterial(s);
            if(mat != null){
                mats.add(mat);
            }
        }

        params.addMaterials(mats);

        Conversable conversable = request.getSender() instanceof Conversable ? (Conversable) request.getSender() : Bukkit.getConsoleSender();

        try {
            m_scatterManager.scatter(scatterType,params,scatterPlayers, conversable);
        } catch (MaxAttemptsReachedException ignored) {
            request.sendMessage(ChatColor.RED+"Hit max attempts at finding enough scatter locations, please try another scatter type and/or parameters");
        }
    }

    /**
     * Ran on /scatter {type} {world} {center} {radius} {minDist} {useTeams} {players/*}*
     * Matches 7 parameters or more
     * @param builder the route builder
     */
    @RouteInfo
    public void scatterCommandDetails(RouteBuilder builder) {
        builder.restrictArgumentCount(7, -1)
                .restrictPermission(SCATTER_COMMAND)
                .restrictCommand("scatter");
    }

    /**
     * List the types loaded
     * @param request the request params
     */
    @CommandMethod
    public void scatterTypesCommand(CommandRequest request){
        String[] types = m_scatterManager.getScatterTypeNames();
        for(String type : types){
            request.sendMessage(ChatColor.GRAY+type);
        }
    }

    /**
     * Run on /scatter types only
     * @param builder the route builder
     */
    @RouteInfo
    public void scatterTypesCommandDetails(RouteBuilder builder) {
        builder.restrictPermission(SCATTER_COMMAND)
                .restrictSenderType(SenderType.PLAYER, SenderType.CONSOLE, SenderType.REMOTE_CONSOLE)
                .restrictStartsWith("types")
                .restrictCommand("scatter");
    }

}
