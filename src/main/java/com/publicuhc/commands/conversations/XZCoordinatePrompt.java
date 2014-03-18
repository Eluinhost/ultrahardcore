package com.publicuhc.commands.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import com.publicuhc.ultrahardcore.util.SimplePair;

public abstract class XZCoordinatePrompt extends ValidatingPrompt {

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        String[] parts = s.split(",");
        if(parts.length != 2){
            return false;
        }
        try{
            //noinspection ResultOfMethodCallIgnored
            Double.valueOf(parts[0]);
            //noinspection ResultOfMethodCallIgnored
            Double.valueOf(parts[1]);
            return true;
        }catch(NumberFormatException ignored){
            return false;
        }
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        String[] parts = s.split(",");
        double x = Double.valueOf(parts[0]);
        double z = Double.valueOf(parts[1]);
        return acceptValidatedInput(conversationContext, new SimplePair<Double, Double>(x,z));
    }

    /**
     * Accept the validatied input
     * @param conversationContext the context
     * @param coords the coordinates supplied
     * @return next prompt
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext conversationContext, SimplePair<Double,Double> coords);
}
