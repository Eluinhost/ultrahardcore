package uk.co.eluinhost.ultrahardcore.commands.scatter;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.commands.SenderType;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.commands.SimpleCommand;
import uk.co.eluinhost.ultrahardcore.scatter.Parameters;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ScatterCommand extends SimpleCommand {

    public static final String SCATTER_COMMAND = "UHC.scatter";
    private static final int CONVERSATION_TIMEOUT = 20;

    private final ConversationFactory m_conversationFactory;

    private final ScatterManager m_scatterManager;

    /**
     * @param configManager the config manager
     * @param scatterManager the scatter manager
     */
    @Inject
    private ScatterCommand(ConfigManager configManager, ScatterManager scatterManager) {
        super(configManager);
        m_scatterManager = scatterManager;
        Map<Object,Object> init = new HashMap<Object, Object>();
        init.put(ScatterEndPrompt.MANAGER,scatterManager);
        m_conversationFactory = new ConversationFactory(UltraHardcore.getInstance())
                .withEscapeSequence("cancel")
                .withPrefix(new ScatterPrefix())
                .withTimeout(CONVERSATION_TIMEOUT)
                .withLocalEcho(false)
                .withFirstPrompt(new ScatterTypePrompt(m_scatterManager))
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
        m_conversationFactory.buildConversation((Conversable) request.getSender());
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
        Iterable<Player> scatterPlayers = new LinkedList<Player>();
        //TODO parse list of players

        Parameters params = new Parameters(new Location(world,x,0,z));
        params.setRadius(radius);
        params.setMinimumDistance(minDistance);
        params.setAsTeam(asTeam);

        //TODO add allowed blocks somewhere

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
