/*
 * DeathBan.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.pluginfeatures.deathbans;

import com.publicuhc.ultrahardcore.util.WordsUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathBan implements ConfigurationSerializable {
    private final UUID m_playerID;
    private final long m_unbanTime;
    private final String m_groupName;

    /**
     * Represents an active ban on a player
     * @param playerID the banned player's UUID
     * @param unbanTime the unix timestamp when the ban ends
     * @param message the message to tell the player
     */
    public DeathBan(UUID playerID, long unbanTime, String message) {
        m_playerID = playerID;
        m_unbanTime = unbanTime;
        m_groupName = message;
    }

    /**
     * Deserializes from the config file
     * @param ser map
     */
    @SuppressWarnings("unused")
    public DeathBan(Map<String,Object> ser){
        m_playerID = UUID.fromString((String) ser.get("playerID"));
        m_unbanTime = (Long) ser.get("unbanTime");
        m_groupName = (String) ser.get("groupName");
    }

    /**
     * @return UNIX timestamp of unban time
     */
    public long getUnbanTime() {
        return m_unbanTime;
    }

    /**
     * @return the banned player's ID
     */
    public UUID getPlayerID() {
        return m_playerID;
    }

    /**
     * @return the group name for this death ban
     */
    public String getGroupName() {
        return m_groupName;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("playerName", m_playerID.toString());
        map.put("unbanTime", m_unbanTime);
        map.put("groupName", m_groupName);
        return map;
    }

    /**
     * Process a login event and disallow login if it matches this ban
     * @param ple the player login event
     * @return true if the player should be unbanned, false if no action
     */
    public boolean processPlayerLoginEvent(PlayerLoginEvent ple){
        if(ple.getPlayer().getUniqueId().equals(m_playerID)){
            if(System.currentTimeMillis() >= getUnbanTime()){
                return true;
            }
            ple.disallow(PlayerLoginEvent.Result.KICK_BANNED, getGroupName().replaceAll("%timeleft", WordsUtil.formatTimeLeft(getUnbanTime())));
        }
        return false;
    }
}