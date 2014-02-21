package uk.co.eluinhost.ultrahardcore.game;

import org.bukkit.Bukkit;

import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.exceptions.games.GameIDConflictException;
import uk.co.eluinhost.ultrahardcore.exceptions.games.GameIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.games.GameNotRunningException;

/**
 * Handles the starting and running of games in UHC
 * @author Graham
 *
 */

@SuppressWarnings("unused")
public class GameManager {

	/**
	 * Stores all of the loaded game types
	 */
	private static UHCGameList UHCGames = new UHCGameList();
		
	private static boolean gameInProgress = false;

	public static void addGameType(UHCGame game) throws GameIDConflictException{
		String ID = game.getGameID();
		for(UHCGame gt : UHCGames){
			if(gt.getGameID().equals(ID)){
				throw new GameIDConflictException();
			}
		}
		UHCGames.add(game);
		Bukkit.getPluginManager().registerEvents(game, UltraHardcore.getInstance());
	}
		
	public void setActiveGame(String ID) throws GameIDNotFoundException{
		for(UHCGame u : UHCGames){
			if(u.getGameID().equals(ID)){
				//unload the current game
				UHCGame currentGame = UHCGames.getCurrentGame();
				//call the on stop method
				currentGame.onGameStop();
				//set it to be not active any more
				currentGame.setCurrentyActive(false);
				//call the unload method
				currentGame.onGameUnload();
				
				//set the active game to the new one
				UHCGames.setCurrentGame(u.getGameID());
				u.setCurrentyActive(true);
				u.onGameLoad();
			}
		}
		throw new GameIDNotFoundException();
	}
	
	/**
	 * Flag the current game as stopped
	 * @throws GameNotRunningException 
	 */
	public static void finishCurrentGame() throws GameNotRunningException{
		if(gameInProgress){
			UHCGames.getCurrentGame().onGameStop();
			gameInProgress = false;
		}else{
			throw new GameNotRunningException();
		}
	}
}
