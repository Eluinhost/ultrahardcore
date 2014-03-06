package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.commands.SenderType;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;

import java.util.List;

public class ScatterCommand {

    public static final String SCATTER_COMMAND = "UHC.scatter";
    private static final int CONVERSATION_TIMEOUT = 20;

    private final ConversationFactory m_conversationFactory = new ConversationFactory(UltraHardcore.getInstance())
            .withEscapeSequence("cancel")
            .withPrefix(new ScatterPrefix())
            .withTimeout(CONVERSATION_TIMEOUT)
            .withLocalEcho(false)
            .withFirstPrompt(new ScatterTypePrompt())
            .withModality(false)
            .addConversationAbandonedListener(new ScatterAbandonListener());

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
        //TODO process and do things
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
        ScatterManager manager = ScatterManager.getInstance();
        List<String> types = manager.getScatterTypeNames();
        for(String type : types){
            request.sendMessage(ChatColor.GRAY+type);
        }
    }

}
