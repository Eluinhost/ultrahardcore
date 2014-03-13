package uk.co.eluinhost.ultrahardcore.scatter;

import java.util.*;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import uk.co.eluinhost.ultrahardcore.scatter.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.scatter.exceptions.ScatterTypeConflictException;
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;
import uk.co.eluinhost.configuration.ConfigManager;

import javax.annotation.Nullable;

public class ScatterManager implements Runnable {

    private final int m_maxTries;
    private final int m_scatterDelay;

    private final List<AbstractScatterType> m_scatterTypes = new ArrayList<AbstractScatterType>();

    private final Protector m_protector = new Protector();

    private final LinkedList<Teleporter> m_remainingTeleports = new LinkedList<Teleporter>();

    private int m_jobID = -1;

    private final Plugin m_plugin;

    public ScatterManager(Plugin plugin, ConfigManager configManager){
        m_plugin = plugin;
        //set up default config
        FileConfiguration config = configManager.getConfig();
        m_maxTries = config.getInt("scatter.maxtries");
        m_scatterDelay = config.getInt("scatter.delay");

        //register ourselves for events
        Bukkit.getServer().getPluginManager().registerEvents(m_protector, plugin);
    }

    @Nullable
    private Conversable m_commandSender;

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
            if (scatterType.equals(type)) {
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
    public String[] getScatterTypeNames() {
        List<String> r = new ArrayList<String>();
        for (AbstractScatterType st : m_scatterTypes) {
            r.add(st.getScatterID());
        }
        return r.toArray(new String[r.size()]);
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
     */
    private void addTeleportMappings(Collection<Teleporter> ptm, Conversable sender) {
        if (m_jobID == -1) {
            m_remainingTeleports.addAll(ptm);
            m_jobID = Bukkit.getScheduler().scheduleSyncDelayedTask(m_plugin, this);
            m_commandSender = sender;
            sender.sendRawMessage("Starting to scatter all players, teleports are " + m_scatterDelay + " ticks apart");
        }
    }

    /**
     * @return unmodifiable list of teleports left to process
     */
    public Iterable<Teleporter> getRemainingTeleports() {
        return Collections.unmodifiableList(m_remainingTeleports);
    }

    /**
     * @return the maximum amount of tries to scatter
     */
    public int getMaxTries() {
        return m_maxTries;
    }

    /**
     * Scatter the players
     * @param type the scatter logic to use
     * @param params the parameters to scatter with
     * @param players the player to scatter
     * @param sender the sender who issued the command to be kept updated
     * @throws MaxAttemptsReachedException if scatter couldn't complete
     */
    public void scatter(AbstractScatterType type, Parameters params, Iterable<Player> players, Conversable sender) throws MaxAttemptsReachedException {
         /*
         * get the right amount of people to scatter
         */
        Map<String, ArrayList<Player>> teams = new HashMap<String, ArrayList<Player>>();
        Collection<Player> noteams = new ArrayList<Player>();

        //if its a team scatter
        if (params.isAsTeam()) {
            //get the scoreboard to query teams
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

            //loop through all the players
            Iterator<Player> playerIterator = players.iterator();
            while(playerIterator.hasNext()){
                Player player = playerIterator.next();

                //get the team
                Team team = scoreboard.getPlayerTeam(player);

                //if they have a team
                if(team != null){
                    //attempt to get the current list for the team and make it if it doesnt exist
                    ArrayList<Player> teamList = teams.get(team.getName());
                    if(null == teamList){
                        teamList = new ArrayList<Player>();
                        teams.put(team.getName(),teamList);
                    }
                    //add the player to the list and remove it from the processing list
                    teamList.add(player);
                    playerIterator.remove();
                }
            }
        }

        //all players here have no team or not a team scatter
        for (Player player : players) {
            noteams.add(player);
        }

        int numberOfPorts = noteams.size() + teams.keySet().size();

        List<Location> teleportLocations = type.getScatterLocations(params,numberOfPorts);

        Iterator<Location> teleportIterator = teleportLocations.iterator();
        Collection<Teleporter> teleporters = new ArrayList<Teleporter>();

        for (Player player : noteams) {
            Location next = teleportIterator.next();
            teleporters.add(new SafeTeleporter(player, next));
        }
        for (Map.Entry<String, ArrayList<Player>> teamList : teams.entrySet()) {
            Location next = teleportIterator.next();
            for (Player player : teamList.getValue()) {
                SafeTeleporter teleporter = new SafeTeleporter(player,next);
                teleporter.setTeam(teamList.getKey());
                teleporters.add(teleporter);
            }
        }
        addTeleportMappings(teleporters, sender);
    }

    @Override
    public void run() {
        if(m_remainingTeleports.isEmpty()){
            try {
                if (m_commandSender != null) {
                    m_commandSender.sendRawMessage(ChatColor.GOLD + "All players now scattered!");
                }
            } catch (RuntimeException ignored) {}
            m_commandSender = null;
            m_jobID = -1;
            return;
        }
        Teleporter ptm = m_remainingTeleports.pollFirst();
        ptm.teleport();
        Bukkit.getScheduler().scheduleSyncDelayedTask(m_plugin,this,m_scatterDelay);
    }
}
