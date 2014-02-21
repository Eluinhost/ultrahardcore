package uk.co.eluinhost.ultrahardcore.bans;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class DeathBan implements ConfigurationSerializable {
    private String playerName;
    private long unbanTime;
    private String groupName;

    public DeathBan(String playerName, long unbanTime, String message) {
        this.playerName = playerName;
        this.unbanTime = unbanTime;
        this.groupName = message;
    }

    @SuppressWarnings("unused")
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