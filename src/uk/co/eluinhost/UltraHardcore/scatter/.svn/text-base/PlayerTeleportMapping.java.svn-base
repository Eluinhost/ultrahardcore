package uk.co.eluinhost.UltraHardcore.scatter;

import org.bukkit.Location;

public class PlayerTeleportMapping {

	private String playerName;
	private int amountTried = 0;
	private Location location;
	private String teamName = null;
	
	public PlayerTeleportMapping(String name,Location loc,String teamName){
		setPlayerName(name);
		setLocation(loc);
		setTeamName(teamName);
	}

	public int getAmountTried() {
		return amountTried;
	}

    @SuppressWarnings("unused")
	public void setAmountTried(int amountTried) {
		this.amountTried = amountTried;
	}
	
	public void incrementAmountTried(){
		amountTried++;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
}
