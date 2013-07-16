package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class DeathBan implements ConfigurationSerializable {
    private String playerName;
    private long unbanTime;
    private String groupName;

    public DeathBan(String playerName, DeathBanGroup d) {
        this.playerName = playerName;
        this.unbanTime = System.currentTimeMillis()+d.getDuration();
        this.groupName = d.getGroupName();
    }

    public DeathBan(Map<String,Object> ser){
        playerName = (String) ser.get("playerName");
        unbanTime = (Long) ser.get("unbanTime");
        groupName = (String) ser.get("groupName");
    }

    public long getUnbanTime() {
        return unbanTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("playerName",playerName);
        m.put("unbanTime",unbanTime);
        m.put("groupName",groupName);
        return m;
    }
}