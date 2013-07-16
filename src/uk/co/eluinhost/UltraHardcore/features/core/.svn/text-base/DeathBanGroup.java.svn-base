package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class DeathBanGroup{
    private String groupName;
    private long duration;
    private String message;

    public static final String DEFAULT_MESSAGE = "You are banned! You will be unbanned in%timeleft";

    public DeathBanGroup(String groupName, String message, long duration) {
        this.groupName = groupName;
        this.duration = duration;
        this.message = message;
    }

    public long getDuration() {
        return duration;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString(){
        return "Name: "+groupName+" Duration: "+DeathBansFeature.formatTimeLeft(System.currentTimeMillis() + duration)+" Message: "+message;
    }

    public Permission getPermission(){
        return new Permission(
                "UHC.deathban.group."+getGroupName(),
                "Put the player in the group "+getGroupName()+" for death bans",
                PermissionDefault.FALSE
        );
    }
}