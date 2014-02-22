package uk.co.eluinhost.ultrahardcore.scatter;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.scatter.ScatterTypeConflictException;
import uk.co.eluinhost.ultrahardcore.scatter.types.EvenCircumferenceType;
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomCircularType;
import uk.co.eluinhost.ultrahardcore.scatter.types.RandomSquareType;

//TODO make not a utility class
//TODO use a better way of restricting to one command at a time
public class ScatterManager {

    public static final int MAX_TRIES = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.SCATTER_MAX_TRIES);
    public static final int MAX_ATTEMPTS = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.SCATTER_MAX_ATTEMPTS);
    public static final int SCATTER_DELAY = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.SCATTER_DELAY);

    private static final AbstractList<AbstractScatterType> SCATTER_TYPES = new ArrayList<AbstractScatterType>();

    private static final ScatterProtector SCATTER_PROTECTOR = new ScatterProtector();

    private static final LinkedList<PlayerTeleportMapping> REMAINING_TELEPORTS = new LinkedList<PlayerTeleportMapping>();

    private int m_jobID = -1;
    private CommandSender m_commandSender;

    public ScatterManager(){
        Bukkit.getServer().getPluginManager().registerEvents(SCATTER_PROTECTOR, UltraHardcore.getInstance());
        try {
            addScatterType(new EvenCircumferenceType());
            addScatterType(new RandomCircularType());
            addScatterType(new RandomSquareType());
        } catch (ScatterTypeConflictException ignored) {
            Bukkit.getLogger().severe("Conflict error when loading default scatter types!");
        }
    }

    public static boolean isScatterInProgress() {
        return !REMAINING_TELEPORTS.isEmpty();
    }

    public static void addScatterType(AbstractScatterType type) throws ScatterTypeConflictException {
        for (AbstractScatterType scatterType : SCATTER_TYPES) {
            if (scatterType.getScatterID().equals(type.getScatterID())) {
                throw new ScatterTypeConflictException();
            }
        }
        SCATTER_TYPES.add(type);
    }

    public static AbstractScatterType getScatterType(String scatterID) {
        for (AbstractScatterType st : SCATTER_TYPES) {
            if (st.getScatterID().equals(scatterID)) {
                return st;
            }
        }
        return null;
    }

    public static List<AbstractScatterType> getScatterTypes() {
        return Collections.unmodifiableList(SCATTER_TYPES);
    }

    public static List<String> getScatterTypeNames() {
        ArrayList<String> r = new ArrayList<String>();
        for (AbstractScatterType st : SCATTER_TYPES) {
            r.add(st.getScatterID());
        }
        return r;
    }

    public static void teleportSafe(Player p, Location loc) {
        loc.getChunk().load(true);
        p.teleport(loc);
        SCATTER_PROTECTOR.addPlayer(p.getName(), loc);
    }

    public void addTeleportMappings(Collection<PlayerTeleportMapping> ptm, CommandSender sender) {
        if (m_jobID == -1) {
            REMAINING_TELEPORTS.addAll(ptm);
            m_jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(), new ScatterRunable(), 0, SCATTER_DELAY);
            if (m_jobID == -1) {
                sender.sendMessage(ChatColor.RED + "Error scheduling scatter");
                REMAINING_TELEPORTS.clear();
            } else {
                m_commandSender = sender;
                sender.sendMessage("Starting to scatter all players, teleports are " + SCATTER_DELAY + " ticks apart");
            }
        }
    }

    public static Iterable<PlayerTeleportMapping> getRemainingTeleports() {
        return Collections.unmodifiableList(REMAINING_TELEPORTS);
    }

    private class ScatterRunable implements Runnable {
        private boolean teleportPlayer(PlayerTeleportMapping ptm) {
            Player p = Bukkit.getPlayerExact(ptm.getPlayerName());
            if (p == null) {
                return false;
            }
            Location loc = ptm.getLocation();
            loc.add(0, 2, 0);
            teleportSafe(p, loc);
            p.sendMessage(ChatColor.GOLD + "You were teleported "
                    + (ptm.getTeamName() == null ? "solo" : "with team " + ptm.getTeamName())
                    + " to " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
            return true;
        }

        @Override
        public void run() {
            PlayerTeleportMapping ptm = REMAINING_TELEPORTS.pollFirst();
            if (ptm == null) {
                try {
                    if (m_commandSender != null) {
                        m_commandSender.sendMessage(ChatColor.GOLD + "All players now scattered!");
                    }
                } catch (Exception ignored) {
                }
                m_commandSender = null;
                Bukkit.getScheduler().cancelTask(m_jobID);
                m_jobID = -1;
                return;
            }
            if (!teleportPlayer(ptm)) {
                ptm.incrementAmountTried();
                if (ptm.getAmountTried() > MAX_ATTEMPTS) {
                    if (m_commandSender != null) {
                        m_commandSender.sendMessage(ChatColor.RED + "Failed to scatter " + ptm.getPlayerName() + " after " + MAX_ATTEMPTS + ", giving up");
                    }
                } else {
                    REMAINING_TELEPORTS.add(ptm);
                }
            }
        }
    }
}
