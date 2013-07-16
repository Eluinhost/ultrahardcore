package uk.co.eluinhost.UltraHardcore.game;

import java.util.ArrayList;

import uk.co.eluinhost.UltraHardcore.exceptions.GameIDNotFoundException;

@SuppressWarnings("serial")
public class UHCGameList extends ArrayList<UHCGame>{

	private int currentGame = -1;

	public UHCGame getCurrentGame() {
		if(currentGame > -1){
			return get(currentGame);
		}else{
			return null;
		}
	}
	
	public void setCurrentGame(String gameID) throws GameIDNotFoundException{
		for(int i = 0;i<size();i++){
			if(get(i).getGameID().equals(gameID)){
				currentGame = i;
			}
		}
		throw new GameIDNotFoundException();
	}
}
