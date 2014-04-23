package com.publicuhc.ultrahardcore.commands.scatter;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.util.SimplePair;
import com.publicuhc.ultrahardcore.scatter.Parameters;
import com.publicuhc.ultrahardcore.scatter.ScatterManager;
import com.publicuhc.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;
import com.publicuhc.ultrahardcore.scatter.types.AbstractScatterType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ScatterEndPrompt extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(final ConversationContext conversationContext) {
        World world = (World) conversationContext.getSessionData(ScatterWorldPrompt.WORLD_DATA);
        SimplePair<Double,Double> center = (SimplePair<Double, Double>) conversationContext.getSessionData(ScatterCenterPrompt.CENTER_DATA);
        Double radius = (Double) conversationContext.getSessionData(ScatterRadiusPrompt.RADIUS_DATA);
        Double minDist = (Double) conversationContext.getSessionData(ScatterMinDistancePrompt.MIN_DIST_DATA);
        final Set<Player> players = (Set<Player>) conversationContext.getSessionData(ScatterPlayerPrompt.PLAYERS_DATA);
        final AbstractScatterType type = (AbstractScatterType) conversationContext.getSessionData(ScatterTypePrompt.TYPE_DATA);
        Boolean asTeam = (Boolean) conversationContext.getSessionData(ScatterUseTeamsPrompt.TEAMS_DATA);
        Plugin plugin = (Plugin) conversationContext.getSessionData(ScatterStartPrompt.PLUGIN);

        final ScatterManager manager = (ScatterManager) conversationContext.getSessionData(ScatterStartPrompt.SCATTER_MANAGER);

        if(manager.isScatterInProgress()){
            return "Scatter failed, there is already a scatter in progress!";
        }

        final Parameters params = new Parameters(new Location(world,center.getKey(),0,center.getValue()));
        params.setAsTeam(asTeam);
        params.setMinimumDistance(minDist);
        params.setRadius(radius);

        Configurator configManager = (Configurator) conversationContext.getSessionData(ScatterStartPrompt.CONFIG_MANAGER);

        FileConfiguration config = configManager.getConfig("main");
        List<String> materials = config.getStringList("scatter.allowedBlocks");
        Collection<Material> mats = new ArrayList<Material>();
        for(String s : materials){
            Material mat = Material.matchMaterial(s);
            if(mat != null){
                mats.add(mat);
            }
        }

        params.addMaterials(mats);

        final Conversable sender = conversationContext.getForWhom();

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){

            @Override
            public void run() {
                try {
                    manager.scatter(type, params, players, conversationContext.getForWhom());
                } catch (MaxAttemptsReachedException ignored) {
                    sender.sendRawMessage("Hit max attempts at finding enough scatter locations, please try another scatter type and/or parameters");
                }
            }
        });
        return "Starting to scatter players";
    }
}
