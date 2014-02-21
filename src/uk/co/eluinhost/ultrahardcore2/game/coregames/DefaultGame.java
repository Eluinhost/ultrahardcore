package uk.co.eluinhost.ultrahardcore.game.coregames;

import uk.co.eluinhost.ultrahardcore.game.UHCGame;

@SuppressWarnings("unused")
public class DefaultGame extends UHCGame {

	public final static String GAME_ID = "Default";
	private final static String GAME_NAME = "Default Game Mode";
	
	/**
	 * Set up the default game, pass game ID and name to UHCGame
	 */
	public DefaultGame(){
		super(GAME_ID,GAME_NAME);
	}

	
}
