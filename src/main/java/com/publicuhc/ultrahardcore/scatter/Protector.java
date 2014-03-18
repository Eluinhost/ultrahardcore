package com.publicuhc.ultrahardcore.scatter;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface Protector extends Listener {

    /**
     * Add the player to be protected for the location given
     * @param player the player to protect
     * @param loc the location to be protected
     */
    void addPlayer(Player player, Location loc);
}
