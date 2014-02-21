package uk.co.eluinhost.ultrahardcore.util;

@SuppressWarnings("UtilityClass")
public class PrintFlags {

    public static final int NONE = 0;
    public static final int PLAYER = 1;
    public static final int TEAM = 2;
    public static final int BOTH = PLAYER | TEAM;

    private PrintFlags() {}

    public static boolean canPrintToTeam(int flags) {
        return (flags & TEAM) == TEAM;
    }

    public static boolean canPrintToPlayer(int flags) {
        return (flags & PLAYER) == PLAYER;
    }
}
