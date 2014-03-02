package uk.co.eluinhost.ultrahardcore.features.playerlist;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigManager;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

/**
 * PlayerListHandler
 * Handles the playerlist health numbers for HANDLED_PLAYERS
 *
 * @author ghowden
 */
public class PlayerListFeature extends UHCFeature {

    public static final String PLAYER_LIST_HEALTH = BASE_PERMISSION + "playerListHealth";

    public PlayerListFeature() {
        super("PlayerList","Player's health shown in player list and under their name");
    }

    //the internal bukkit id for the task
    private int m_taskID = -1;

    //the list of players and their health that we are handling
    private static final Map<Player, Double> HANDLED_PLAYERS = new WeakHashMap<Player, Double>();

    private static final double HEALTH_SCALING = ConfigManager.getInstance().getConfig().getDouble(ConfigNodes.PLAYER_LIST_SCALING);
    private static final boolean ROUND_HEALTH = ConfigManager.getInstance().getConfig().getBoolean(ConfigNodes.PLAYER_LIST_ROUND_HEALTH);
    private static final boolean PLAYER_LIST_COLOURS = ConfigManager.getInstance().getConfig().getBoolean(ConfigNodes.PLAYER_LIST_COLOURS);

    private static final Scoreboard MAIN_SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();

    private static final int MAX_LENGTH_COLOURS = 14;
    private static final int MAX_LENGTH_NO_COLOURS = 16;

    private static final String OBJECTIVE_SCOREBOARD_NAME = "UHCHealth";
    private static final String OBJECTIVE_UNDER_NAME_NAME = "UHCHealthName";
    private static final String OBJECTIVE_TYPE = "dummy";

    private static final double RED_BOUNDARY = 6.0;
    private static final double YELLOW_BOUNDARY = 12.0;

    private Objective m_objectivePlayerList;
    private Objective m_objectiveUnderName;

    //update the players name in the list with the following health number
    public void updatePlayerListHealth(Player player, double health) {
        //TODO add config toggle for using names/display names
        //get the players display name and strip the colour codes from it
        String newName = ChatColor.stripColor(player.getDisplayName());

        //cut the name down to the right length
        newName = newName.substring(0,Math.min(newName.length(),PLAYER_LIST_COLOURS ? MAX_LENGTH_COLOURS : MAX_LENGTH_NO_COLOURS));

        double showHealth = health;

        if (PLAYER_LIST_COLOURS) {
            ChatColor prefix = ChatColor.GREEN;
            if (health <= YELLOW_BOUNDARY) {
                prefix = ChatColor.YELLOW;
            }
            if (health <= RED_BOUNDARY) {
                prefix = ChatColor.RED;
            }
            if (!player.hasPermission(PLAYER_LIST_HEALTH)) {
                prefix = ChatColor.BLUE;
                showHealth = 0.0D;
            }
            newName = prefix+newName;
        }

        //set the player list name
        player.setPlayerListName(newName);

        //if we're rounding health
        if (ROUND_HEALTH) {
            showHealth = Math.ceil(showHealth);
        }

        //set the score for both the player and their display name
        //this allows the score to show under the head of players with a changed name
        m_objectivePlayerList.getScore(Bukkit.getOfflinePlayer(newName)).setScore((int) (showHealth * HEALTH_SCALING));
        m_objectiveUnderName.getScore(Bukkit.getOfflinePlayer(ChatColor.stripColor(player.getDisplayName()))).setScore((int) (showHealth * HEALTH_SCALING));
    }

    /**
     * Update all the players supplied
     * @param onlinePlayers Player[]
     */
    public void updatePlayers(Player[] onlinePlayers) {
        for (Player player : onlinePlayers) {
            //get the existing health
            Double health = HANDLED_PLAYERS.get(player);
            //if there isn't one
            if (health == null) {
                //set it to 0.0 and add the player
                health = 0.0D;
                HANDLED_PLAYERS.put(player, health);
            }
            //if the health has changed
            if (!health.equals(player.getHealth())) {
                //update with the new values
                updatePlayerListHealth(player, player.getHealth());
            }
        }
    }

    @Override
    protected void enableCallback() {
        //set up the timer that runs
        m_taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                UltraHardcore.getInstance(),
                new PlayerListUpdater(),
                1L,
                ConfigManager.getInstance().getConfig().getLong(ConfigNodes.PLAYER_LIST_DELAY)
        );
        //intialize the scoreboard
        initializeScoreboard();
    }

    @Override
    protected void disableCallback() {
        //disable the task if its running
        if (m_taskID >= 0) {
            Bukkit.getScheduler().cancelTask(m_taskID);
            m_taskID = -1;
        }
        //if the scoreboard is there
        if (MAIN_SCOREBOARD != null) {
            //clear the slots we use
            MAIN_SCOREBOARD.clearSlot(DisplaySlot.PLAYER_LIST);
            MAIN_SCOREBOARD.clearSlot(DisplaySlot.BELOW_NAME);
            //reset the player list name for all online players to just the display name
            //TODO this should be getName()?
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.setPlayerListName(p.getDisplayName());
            }
        }
    }

    /**
     * Initialize our scoreboard
     */
    private void initializeScoreboard() {
        //try to make new objectives, throws exception when it already exists
        try {
            MAIN_SCOREBOARD.registerNewObjective(OBJECTIVE_SCOREBOARD_NAME, OBJECTIVE_TYPE);
        } catch (IllegalArgumentException ignored) {}
        try {
            MAIN_SCOREBOARD.registerNewObjective(OBJECTIVE_UNDER_NAME_NAME, OBJECTIVE_TYPE);
        } catch (IllegalArgumentException ignored) {}

        //set the objectives we created
        m_objectivePlayerList = MAIN_SCOREBOARD.getObjective(OBJECTIVE_SCOREBOARD_NAME);
        m_objectiveUnderName = MAIN_SCOREBOARD.getObjective(OBJECTIVE_UNDER_NAME_NAME);

        //set the display name of the under name objective
        m_objectiveUnderName.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.getInstance().getConfig().getString(ConfigNodes.PLAYER_LIST_HEALTH_NAME)).replaceAll("&h", "\u2665"));

        //set the slot for player list health
        m_objectivePlayerList.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        //if under name is enabled
        if (ConfigManager.getInstance().getConfig().getBoolean(ConfigNodes.PLAYER_LIST_UNDER_NAME)) {
            //set it's slot
            m_objectiveUnderName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        } else {
            //get the objective that is below the name
            Objective o = MAIN_SCOREBOARD.getObjective(DisplaySlot.BELOW_NAME);
            //if its our objective clear the slot
            if (o != null && o.getName().equals(OBJECTIVE_UNDER_NAME_NAME)) {
                MAIN_SCOREBOARD.clearSlot(DisplaySlot.BELOW_NAME);
            }
        }
    }

    private class PlayerListUpdater implements Runnable {
        @Override
        public void run() {
            updatePlayers(Bukkit.getOnlinePlayers());
        }
    }
}
