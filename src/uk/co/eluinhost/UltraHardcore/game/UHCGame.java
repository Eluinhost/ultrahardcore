package uk.co.eluinhost.ultrahardcore.game;

import org.bukkit.event.Listener;

import uk.co.eluinhost.ultrahardcore.exceptions.GameNotRunningException;

@SuppressWarnings("unused")
public abstract class UHCGame implements Listener{

	/**
	 * Stores the internal game ID and name for this game type
	 */
	private String GAME_ID = "";
	private String GAME_NAME = "";
	
	private boolean currentyActive = false;
	
	@Override
	public final boolean equals(Object o){
		//if its a uhcgame and it has the same ID as this it is 'equal'
		return o instanceof UHCGame && ((UHCGame) o).getGameID().equals(GAME_ID);
	}

	protected UHCGame(String ID,String name){
		setGameID(ID);
		setGameName(name);
	}

	public final String getGameID() {
		return GAME_ID;
	}

	protected final void setGameID(String gAME_ID) {
		GAME_ID = gAME_ID;
	}

	public final String getGameName() {
		return GAME_NAME;
	}

	protected final void setGameName(String gAME_NAME) {
		GAME_NAME = gAME_NAME;
	}

	public boolean canGameStart(){
		return true;
	}

	public void onGameInit(){}

    public void onGameLoad(){}

	public void onGameStart(){}

	public void onGameStop(){}

	public void onGameUnload(){}

	protected final void finishGame(){
		try {
			GameManager.finishCurrentGame();
		} catch (GameNotRunningException e) {
			e.printStackTrace();
		}
	}

	public final boolean isCurrentyActive() {
		return currentyActive;
	}

	protected final void setCurrentyActive(boolean currentyActive) {
		this.currentyActive = currentyActive;
	}
}
