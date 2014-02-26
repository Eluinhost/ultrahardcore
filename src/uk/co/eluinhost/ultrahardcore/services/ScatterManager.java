package uk.co.eluinhost.ultrahardcore.services;

import java.util.*;
import java.util.logging.Level;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.scatter.Parameters;
import uk.co.eluinhost.ultrahardcore.scatter.PlayerTeleportMapping;
import uk.co.eluinhost.ultrahardcore.scatter.Protector;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.scatter.ScatterTypeConflictException;
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;
import uk.co.eluinhost.ultrahardcore.util.SimplePair;

public class ScatterManager {

    private int m_maxTries;
    private int m_maxAttemtps;
    private int m_scatterDelay;

    @SuppressWarnings("UtilityClass")
    private static class LazyScatterManagerHolder {
        private static final ScatterManager INSTANCE = new ScatterManager();
    }

    public static ScatterManager getInstance(){
        return LazyScatterManagerHolder.INSTANCE;
    }

    private ScatterManager(){
        UltraHardcore plugin = UltraHardcore.getInstance();

        //set up default config
        FileConfiguration config = ConfigManager.getInstance().getConfig();
        m_maxTries = config.getInt(ConfigNodes.SCATTER_MAX_TRIES);
        m_maxAttemtps = config.getInt(ConfigNodes.SCATTER_MAX_ATTEMPTS);
        m_scatterDelay = config.getInt(ConfigNodes.SCATTER_DELAY);

        //register ourselves for events
        Bukkit.getServer().getPluginManager().registerEvents(m_protector, plugin);
    }

    private final List<AbstractScatterType> m_scatterTypes = new ArrayList<AbstractScatterType>();

    private final Protector m_protector = new Protector();

    private final LinkedList<PlayerTeleportMapping> m_remainingTeleports = new LinkedList<PlayerTeleportMapping>();

    private int m_jobID = -1;
    private CommandSender m_commandSender;

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

    public void scatter(AbstractScatterType type, Boolean useTeams, Double radius, Double mindist, SimplePair<Double, Double> coords, World w, List<String> players, CommandSender sender) {
        Parameters sp = new Parameters(w.getName(), coords.getKey(), coords.getValue(), radius);
        sp.setMinDistance(mindist);
        List<String> allowedBlocks = ConfigManager.getConfig(ConfigManager.MAIN).getStringList(ConfigNodes.SCATTER_ALLOWED_BLOCKS);
        LinkedList<Material> materials = new LinkedList<Material>();
        for (String s : allowedBlocks) {
            Material material = Material.matchMaterial(s);
            if (material == null) {
                UltraHardcore.getInstance().getLogger().log(Level.WARNING, "Unknown scatter block " + s);
                continue;
            }
            materials.add(material);
        }
        sp.setAllowedBlocks(materials);

        /*
         * get the right amount of people to scatter
         */
        @SuppressWarnings("unchecked")
        HashMap<String, ArrayList<Player>> teams = new HashMap<String, ArrayList<Player>>();
        AbstractList<Player> noteams = new ArrayList<Player>();

        if (useTeams) {
            Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
            for (String p : players) {
                Player pl = Bukkit.getPlayer(p);
                if (pl == null) {
                    sender.sendMessage(ChatColor.RED + "The player " + p + " is now offline, will not include them for scattering");
                    continue;
                }
                Team t = sb.getPlayerTeam(pl);
                if (t == null) {
                    noteams.add(pl);
                } else {
                    ArrayList<Player> team = teams.get(t.getName());
                    if (team == null) {
                        team = new ArrayList<Player>();
                        teams.put(t.getName(), team);
                    }
                    team.add(pl);
                }
            }
        } else {
            for (String p : players) {
                Player pl = Bukkit.getPlayer(p);
                if (pl == null) {
                    sender.sendMessage(ChatColor.RED + "The player " + p + " is now offline, will not include them for scattering");
                    continue;
                }
                noteams.add(pl);
            }
        }

        int numberOfPorts = noteams.size() + teams.keySet().size();

        List<Location> teleports;
        try {
            teleports = type.getScatterLocations(sp, numberOfPorts);
        } catch (WorldNotFoundException ignored) {
            sender.sendMessage(ChatColor.RED + "The world specified doesn't exist!");
            return;
        } catch (MaxAttemptsReachedException ignored) {
            sender.sendMessage(ChatColor.RED + "Max scatter attempts reached and not all players have valid locations, cancelling scatter");
            return;
        }
        Iterator<Location> teleportIterator = teleports.iterator();
        AbstractList<PlayerTeleportMapping> ptms = new ArrayList<PlayerTeleportMapping>();
        for (Player p : noteams) {
            Location next = teleportIterator.next();
            ptms.add(new PlayerTeleportMapping(p.getName(), next, null));
        }
        for (String teamName : teams.keySet()) {
            Location next = teleportIterator.next();
            for (Player p : teams.get(teamName)) {
                ptms.add(new PlayerTeleportMapping(p.getName(), next, teamName));
            }
        }
        UltraHardcore.getInstance().getScatterManager().addTeleportMappings(ptms, sender);
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
