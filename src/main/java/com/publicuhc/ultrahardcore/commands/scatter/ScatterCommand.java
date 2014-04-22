package com.publicuhc.ultrahardcore.commands.scatter;

import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.commands.SenderType;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.pluginframework.shaded.inject.Inject;
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
     * @param scatterManager the scatter manager
     * @param plugin the plugin to reference
     */
    @Inject
    private ScatterCommand(ConfigManager configManager, ScatterManager scatterManager, Plugin plugin) {
        super(configManager);
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
     * Ran on /iscatter
     * @param request the request params
     */
    @Command(trigger = "iscatter",
            identifier = "InteractiveScatterCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = SCATTER_COMMAND,
            senders = {SenderType.PLAYER, SenderType.CONSOLE})
    public void onInteractiveScatterCommand(CommandRequest request){
        m_conversationFactory.buildConversation((Conversable) request.getSender()).begin();
    }

    /**
     * Ran on /scatter {type} {world} {center} {radius} {minDist} {useTeams} {players/*}*
     * @param request the request params
     */
    @Command(trigger = "scatter",
            identifier = "ScatterCommand",
            minArgs = 7,
            permission = SCATTER_COMMAND)
    public void onScatterCommand(CommandRequest request){
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
                scatterPlayers.addAll(Arrays.asList(Bukkit.getOnlinePlayers()));
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

        FileConfiguration config = getConfigManager().getConfig();
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
     * Ran on /scatter types
     * @param request the request params
     */
    @Command(trigger = "types",
            identifier = "ScatterTypesCommand",
            parentID = "ScatterCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = SCATTER_COMMAND)
    public void onScatterTypesCommand(CommandRequest request){
        String[] types = m_scatterManager.getScatterTypeNames();
        for(String type : types){
            request.sendMessage(ChatColor.GRAY+type);
        }
    }

}
