package uk.co.eluinhost.ultrahardcore.commands.scatter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.scatter.Parameters;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;
import uk.co.eluinhost.ultrahardcore.util.SimplePair;

import java.util.Set;

public class ScatterEndPrompt extends MessagePrompt {

    public static final String MANAGER = "SCATTER_MANAGER";

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        World world = (World) conversationContext.getSessionData(ScatterWorldPrompt.WORLD_DATA);
        SimplePair<Double,Double> center = (SimplePair<Double, Double>) conversationContext.getSessionData(ScatterCenterPrompt.CENTER_DATA);
        Double radius = (Double) conversationContext.getSessionData(ScatterRadiusPrompt.RADIUS_DATA);
        Double minDist = (Double) conversationContext.getSessionData(ScatterMinDistancePrompt.MIN_DIST_DATA);
        Set<Player> players = (Set<Player>) conversationContext.getSessionData(ScatterPlayerPrompt.PLAYERS_DATA);
        AbstractScatterType type = (AbstractScatterType) conversationContext.getSessionData(ScatterTypePrompt.TYPE_DATA);
        Boolean asTeam = (Boolean) conversationContext.getSessionData(ScatterUseTeamsPrompt.TEAMS_DATA);

        ScatterManager manager = (ScatterManager) conversationContext.getSessionData(MANAGER);

        if(manager.isScatterInProgress()){
            return "Scatter failed, there is already a scatter in progress!";
        }

        Parameters params = new Parameters(new Location(world,center.getKey(),0,center.getValue()));
        params.setAsTeam(asTeam);
        params.setMinimumDistance(minDist);
        params.setRadius(radius);

        try {
            manager.scatter(type, params, players, conversationContext.getForWhom());
            return "Starting to scatter players";
        } catch (MaxAttemptsReachedException ignored) {
            return "Hit max attempts at finding enough scatter locations, please try another scatter type and/or parameters";
        }
    }
}
