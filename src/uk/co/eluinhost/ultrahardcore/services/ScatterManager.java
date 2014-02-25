package uk.co.eluinhost.ultrahardcore.services;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.scatter.PlayerTeleportMapping;
import uk.co.eluinhost.ultrahardcore.scatter.Protector;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.scatter.ScatterTypeConflictException;
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;

public class ScatterManager {

    private int m_maxTries;
    private int m_maxAttemtps;
    private int m_scatterDelay;

    private final List<AbstractScatterType> m_scatterTypes = new ArrayList<AbstractScatterType>();

    private final Protector m_protector = new Protector();

    private final LinkedList<PlayerTeleportMapping> m_remainingTeleports = new LinkedList<PlayerTeleportMapping>();

    private int m_jobID = -1;
    private CommandSender m_commandSender = null;

    /**
     * Scatter manager to provide ability to scatter players
     */
    public ScatterManager(){
        UltraHardcore plugin = UltraHardcore.getInstance();

        //set up default config
        FileConfiguration config = plugin.getConfigManager().getConfig();
        m_maxTries = config.getInt(ConfigNodes.SCATTER_MAX_TRIES);
        m_maxAttemtps = config.getInt(ConfigNodes.SCATTER_MAX_ATTEMPTS);
        m_scatterDelay = config.getInt(ConfigNodes.SCATTER_DELAY);

        //register ourselves for events
        Bukkit.getServer().getPluginManager().registerEvents(m_protector, plugin);
    }

    /**
     * @return true if currently busy, false otherwise
     */
    public boolean isScatterInProgress() {
        return !m_remainingTeleports.isEmpty();
    }

    /**
     * Add a scatter type to the system
     * @param type the scatterer to add
     * @throws ScatterTypeConflictException if the scatter ID is already taken
     */
    public void addScatterType(AbstractScatterType type) throws ScatterTypeConflictException {
        for (AbstractScatterType scatterType : m_scatterTypes) {
            //TODO make an equals method in abstractscattertype
            if (scatterType.getScatterID().equals(type.getScatterID())) {
                throw new ScatterTypeConflictException();
            }
        }
        m_scatterTypes.add(type);
    }

    /**
     * @param scatterID the ID to look for
     * @return the scatter type if found or null if not
     */
    public AbstractScatterType getScatterType(String scatterID) {
        for (AbstractScatterType st : m_scatterTypes) {
            if (st.getScatterID().equals(scatterID)) {
                return st;
            }
        }
        return null;
    }

    /**
     * @return unmodifiable list of all scatter types
     */
    public List<AbstractScatterType> getScatterTypes() {
        return Collections.unmodifiableList(m_scatterTypes);
    }

    /**
     * @return a list of all the scatterIDs
     */
    public List<String> getScatterTypeNames() {
        List<String> r = new ArrayList<String>();
        for (AbstractScatterType st : m_scatterTypes) {
            r.add(st.getScatterID());
        }
        return r;
    }

    /**
     * Scatters the player and protects them from damage using Protector
     * @param player the player to scatter
     * @param loc the location to scatter to
     */
    public void teleportSafe(Player player, Location loc) {
        loc.getChunk().load(true);
        player.teleport(loc);
        m_protector.addPlayer(player, loc);
    }

    /**
     * Add a collection of teleports to be queued
     * @param ptm the colelction of teleports
     * @param sender the sender who issued the command to be kept updated
     *               TODO don't want to keep sender here like this
     */
    public void addTeleportMappings(Collection<PlayerTeleportMapping> ptm, CommandSender sender) {
        if (m_jobID == -1) {
            m_remainingTeleports.addAll(ptm);
            m_jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(), new ScatterRunable(), 0, m_scatterDelay);
            if (m_jobID == -1) {
                sender.sendMessage(ChatColor.RED + "Error scheduling scatter");
                m_remainingTeleports.clear();
            } else {
                m_commandSender = sender;
                sender.sendMessage("Starting to scatter all players, teleports are " + m_scatterDelay + " ticks apart");
            }
        }
    }

    /**
     * @return unmodifiable list of teleports left to process
     */
    public Iterable<PlayerTeleportMapping> getRemainingTeleports() {
        return Collections.unmodifiableList(m_remainingTeleports);
    }

    /**
     * @return the maximum amount of tries to scatter
     */
    public int getMaxTries() {
        return m_maxTries;
    }

    /**
     * @param maxTries the max amount of tries to scatter
     */
    public void setMaxTries(int maxTries) {
        m_maxTries = maxTries;
    }

    //TODO move out
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
            PlayerTeleportMapping ptm = m_remainingTeleports.pollFirst();
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
                if (ptm.getAmountTried() > m_maxAttemtps) {
                    if (m_commandSender != null) {
                        m_commandSender.sendMessage(ChatColor.RED + "Failed to scatter " + ptm.getPlayerName() + " after " + m_maxAttemtps + ", giving up");
                    }
                } else {
                    m_remainingTeleports.add(ptm);
                }
            }
        }
    }
}
