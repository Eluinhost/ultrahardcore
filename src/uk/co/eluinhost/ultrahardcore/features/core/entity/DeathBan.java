package uk.co.eluinhost.ultrahardcore.features.core.entity;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class DeathBan implements ConfigurationSerializable {
    private final String m_playerName;
    private final long m_unbanTime;
    private final String m_groupName;

    public DeathBan(String playerName, long unbanTime, String message) {
        m_playerName = playerName;
        m_unbanTime = unbanTime;
        m_groupName = message;
    }

    @SuppressWarnings("unused")
    public DeathBan(Map<String,Object> ser){
        m_playerName = (String) ser.get("playerName");
        m_unbanTime = (Long) ser.get("unbanTime");
        m_groupName = (String) ser.get("groupName");
    }

    public long getUnbanTime() {
        return m_unbanTime;
    }

    public String getPlayerName() {
        return m_playerName;
    }

    public String getGroupName() {
        return m_groupName;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("playerName", m_playerName);
        map.put("unbanTime", m_unbanTime);
        map.put("groupName", m_groupName);
        return map;
    }
}