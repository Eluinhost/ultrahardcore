package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;

public class ScatterTypePrompt extends FixedSetPrompt {

    public static final String TYPE_DATA = "scatter_type";
    private final ScatterManager m_manager;

    /**
     * Allows any of the loaded scatter type names
     * @param manager the scatter manager
     */
    public ScatterTypePrompt(ScatterManager manager){
        super(manager.getScatterTypeNames());
        m_manager = manager;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        conversationContext.setSessionData(TYPE_DATA,m_manager.getScatterType(s));
        return new ScatterWorldPrompt();
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter type of scatter to use: "+formatFixedSet();
    }
}
