package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import uk.co.eluinhost.commands.conversations.WorldNamePrompt;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class ScatterWorldPrompt extends WorldNamePrompt {

    public static final String WORLD_DATA = "world";

    /**
     * Allow any loaded world
     */
    public ScatterWorldPrompt(){
        super(ServerUtil.getWorldNames());
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter the name of the world to scatter into: ";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, World world) {
        conversationContext.setSessionData(WORLD_DATA,world);
        return new ScatterCenterPrompt();
    }
}
