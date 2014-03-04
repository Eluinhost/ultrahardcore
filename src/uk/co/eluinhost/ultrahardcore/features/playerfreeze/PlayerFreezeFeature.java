package uk.co.eluinhost.ultrahardcore.features.playerfreeze;

import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

import java.util.HashSet;
import java.util.Set;

public class PlayerFreezeFeature extends UHCFeature {

    private Set<String> m_players = new HashSet<String>();

    /**
     * handles frozen players
     */
    public PlayerFreezeFeature() {
        super("PlayerFreeze", "Allows for freezing players in place");
    }

    /**
     * @param playerName the player name to freeze
     */
    public void addPlayer(String playerName){
        m_players.add(playerName);
        //TODO stop movement
    }

    /**
     * @param playerName the player name
     */
    public void removePlayer(String playerName){
        m_players.remove(playerName);
        //TODO allow movement
    }

    /**
     * Remove all from the frozen list
     */
    public void unfreezeAll(){
        //TODO allow movement for all
        m_players.clear();
    }
}
