package uk.co.eluinhost.ultrahardcore.config;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

//TODO move into relevant classes
public class PermissionNodes {

    public static final String HEAL_NODE = "UHC.heal";
    public static final String HEAL_ALL = HEAL_NODE + ".all";
    public static final String HEAL_SELF = HEAL_NODE + ".self";
    public static final String HEAL_OTHER = HEAL_NODE + ".other";
    public static final String HEAL_ANNOUNCE = HEAL_NODE + ".announce";

    public static final String FEED_NODE = "UHC.feed";
    public static final String FEED_ALL = FEED_NODE + ".all";
    public static final String FEED_SELF = FEED_NODE + ".self";
    public static final String FEED_OTHER = FEED_NODE + ".other";
    public static final String FEED_ANNOUNCE = FEED_NODE + ".announce";

    public static final String FEATURE_TOGGLE = "UHC.feature.toggle";
    public static final String FEATURE_LIST = "UHC.feature.list";

    public static final String ANTIFREEZE = "UHC.freeze.antifreeze";
    public static final String FREEZE_PERMISSION = "UHC.freeze.command";

    public static final String SCATTER_COMMAND = "UHC.scatter";

    public static final String GENERATE_BORDER = "UHC.generateborder";

    //public static final String TP_ALL = "UHC.tpall";

    public static final String CLEAR_INVENTORY_SELF = "UHC.ci.self";
    public static final String CLEAR_INVENTORY_OTHER = "UHC.ci.other";
    public static final String CLEAR_INVENTORY_IMMUNE = "UHC.ci.immune";

    public static final String RANDOM_TEAMS = "UHC.teams.random";
    public static final String RANDOM_TEAMS_CLEAR = "UHC.teams.clear";
    public static final String LIST_TEAMS = "UHC.teams.list";
    public static final String RANDOM_TEAMS_CREATE = "UHC.teams.create";
    public static final String RANDOM_TEAMS_REMOVE_UHC = "UHC.teams.remove.UHC";
    public static final String RANDOM_TEAMS_REMOVE_ALL = "UHC.teams.remove.all";
    public static final String RANDOM_TEAMS_JOIN_UHC = "UHC.teams.join.UHC";
    public static final String RANDOM_TEAMS_JOIN_ALL = "UHC.teams.join.all";
    public static final String RANDOM_TEAMS_JOIN_OTHER = "UHC.teams.join.other";
    public static final String RANDOM_TEAMS_LEAVE_SELF = "UHC.teams.leave.self";
    public static final String RANDOM_TEAMS_LEAVE_OTHER = "UHC.teams.leave.other";
    public static final String RANDOM_TEAMS_EMPTY = "UHC.teams.empty";


    public static final String DEATH_BAN_BAN = "base_death_ban.unban";
    public static final String DEATH_BAN_UNBAN = "base_death_ban.ban";

    public static final String GIVE_DROPS = "UHC.givedrops";
    public static final String TIMER_COMMAND = "UHC.timer";

    public static Permission TP_ALL = new Permission(
            "UHC.tpall",
            "Allows player to teleport players to players/locations",
            PermissionDefault.OP);
}
