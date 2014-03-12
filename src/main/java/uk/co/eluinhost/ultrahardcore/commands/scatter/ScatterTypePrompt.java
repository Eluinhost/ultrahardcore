package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;

public class ScatterTypePrompt extends FixedSetPrompt {

    public static final String TYPE_DATA = "scatter_type";

    /**
     * Allows any of the loaded scatter type names
     */
    public ScatterTypePrompt(){
        //noinspection AssignmentToSuperclassField
        fixedSet = ScatterManager.getInstance().getScatterTypeNames();
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        conversationContext.setSessionData(TYPE_DATA,ScatterManager.getInstance().getScatterType(s));
        return new ScatterWorldPrompt();
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return "Enter type of scatter to use: "+formatFixedSet();
    }
}
