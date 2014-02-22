package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.scatter.MaxAttemptsReachedException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.scatter.PlayerTeleportMapping;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterParams;
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;
import uk.co.eluinhost.ultrahardcore.util.SimplePair;

import java.util.*;
import java.util.logging.Level;

//TODO this thing sucks balls
public class ScatterPrompt extends StringPrompt {

    private static final int MAX_DISTANCE = 2000000;

    enum DATA {
        TYPE, TEAMS, RADIUS, MINDIST, WORLD, COORDS, PLAYERS
    }

    private static DATA getNextMissing(ConversationContext context) {
        for (DATA data : DATA.values()) {
            if (context.getSessionData(data) == null) {
                return data;
            }
        }
        return null;
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        if (ScatterManager.isScatterInProgress()) {
            conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "A scatter command has already been started, please wait until it completes and try again");
            return END_OF_CONVERSATION;
        }
        DATA data = getNextMissing(conversationContext);
        if (data == null) {
            return END_OF_CONVERSATION;
        }
        //todo really?
        switch (data) {
            case TYPE:
                if ("types".equals(s)) {
                    List<String> ty = getTypesOutput();
                    for (String type : ty) {
                        conversationContext.getForWhom().sendRawMessage(type);
                    }
                } else {
                    AbstractScatterType st = parseScatterType(s);
                    if (st == null) {
                        conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "That isn't a valid scatter type!");
                    } else {
                        conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA + "Using the scatter type: " + st.getScatterID());
                        conversationContext.setSessionData(DATA.TYPE, st);
                    }
                }
                break;
            case TEAMS:
                Boolean useTeams = parseTeams(s);
                if (useTeams == null) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "You can only use yes/no here!");
                } else {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA + "Will scatter players " + (useTeams ? "using" : " disregarding ") + " teams.");
                    conversationContext.setSessionData(DATA.TEAMS, useTeams);
                }
                break;
            case RADIUS:
                Double radius = parseRadius(s);
                if (radius == null) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "That isn't a number!");
                } else {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA + "Using radius: " + radius);
                    conversationContext.setSessionData(DATA.RADIUS, radius);
                }
                break;
            case MINDIST:
                Double mindist = parseMinDist(s);
                if (mindist == null) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "That isn't a number!");
                } else {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA + "Using minimum distance: " + mindist);
                    conversationContext.setSessionData(DATA.MINDIST, mindist);
                }
                break;
            case WORLD:
                World world = parseWorld(s);
                if (world == null) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Unknown world!");
                } else {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA + "Using world: " + world.getName());
                    conversationContext.setSessionData(DATA.WORLD, world);
                }
                break;
            case COORDS:
                SimplePair<Double, Double> coords = parseCoords(s);
                if (coords == null) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Unknown coords, use the format x,z");
                } else {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA + "Using centre coords: " + coords.getKey() + "," + coords.getValue());
                    conversationContext.setSessionData(DATA.COORDS, coords);
                }
                break;
            case PLAYERS:
                SimplePair<List<String>, List<String>> playerList = parsePlayers(s);
                if (!playerList.getValue().isEmpty()) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Couldn't find the players " + Arrays.toString(playerList.getValue().toArray()));
                }
                if (playerList.getKey().isEmpty()) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "There are no players to scatter!");
                } else {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA + "Will scatter players: " + Arrays.toString(playerList.getKey().toArray()));
                    conversationContext.setSessionData(DATA.PLAYERS, playerList.getKey());
                }
        }
        if (getNextMissing(conversationContext) == null) {
            World w = (World) conversationContext.getSessionData(DATA.WORLD);
            @SuppressWarnings("unchecked")
            SimplePair<Double, Double> coords = (SimplePair<Double, Double>) conversationContext.getSessionData(DATA.COORDS);
            Double r = (Double) conversationContext.getSessionData(DATA.RADIUS);
            Double min = (Double) conversationContext.getSessionData(DATA.MINDIST);
            @SuppressWarnings("unchecked")
            List<String> allPlayersS = (List<String>) conversationContext.getSessionData(DATA.PLAYERS);
            Boolean usingTeams = (Boolean) conversationContext.getSessionData(DATA.TEAMS);
            AbstractScatterType scatterType = (AbstractScatterType) conversationContext.getSessionData(DATA.TYPE);
            CommandSender sender = (CommandSender) conversationContext.getSessionData("SENDER");
            scatter(scatterType, usingTeams, r, min, coords, w, allPlayersS, sender);
            return END_OF_CONVERSATION;
        }
        return new ScatterPrompt();
    }

    private static final String SUFFIX = "Wait 60 seconds or type 'cancel' to cancel this command.";

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        DATA data = getNextMissing(conversationContext);
        if (data == null) {
            return "ERROR IN COMMAND";
        }
        switch (getNextMissing(conversationContext)) {
            case TYPE:
                return "Enter scatter type to use or 'types' for a list of types. " + SUFFIX;
            case TEAMS:
                return "Should players be scattered as teams? yes/no. " + SUFFIX;
            case RADIUS:
                return "Enter the radius people should be scattered within. " + SUFFIX;
            case MINDIST:
                return "Enter the minimum distance allowed between players/teams. " + SUFFIX;
            case WORLD:
                List<World> worlds = Bukkit.getWorlds();
                StringBuilder sb = new StringBuilder();
                sb.append("Enter the name of the world to scatter into. Loaded worlds:");
                for (World w : worlds) {
                    sb.append(" ");
                    sb.append(w.getName());
                }
                sb.append(" ");
                sb.append(SUFFIX);
                return sb.toString();
            case COORDS:
                Location loc = ((World) conversationContext.getSessionData(DATA.WORLD)).getSpawnLocation();
                return "Enter the centre coords to use, chosen world spawn coordinates are: "
                        + loc.getX() + "," + loc.getZ() + " " + SUFFIX;
            case PLAYERS:
                return "Enter the players to teleport separated by spaces or '*' to scatter all players " + SUFFIX;
        }
        return "ERROR IN COMMAND";
    }

    /**
     * Get the scatter type for the string given
     *
     * @param s the input string
     * @return the ScatterType if exists, null otherwise
     */
    public static AbstractScatterType parseScatterType(String s) {
        return ScatterManager.getScatterType(s);
    }

    /**
     * Parse whether to use teams or not
     *
     * @param s The input string
     * @return true if 'yes', false if 'no', null otherwise
     */
    public static Boolean parseTeams(String s) {
        if ("yes".equalsIgnoreCase(s)) {
            return true;
        }
        if ("no".equalsIgnoreCase(s)) {
            return false;
        }
        return null;
    }

    /**
     * Parse the radius
     *
     * @param s the input string
     * @return radius as a double, null if not a number or greater than MAX_DISTANCE
     */
    public static Double parseRadius(String s) {
        try {
            Double d = Double.parseDouble(s);
            if (d >= 0 && d < MAX_DISTANCE) {
                return d;
            }
            return null;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Parse the min dist
     *
     * @param s the input string
     * @return the mindist as double, null if not a number
     */
    public static Double parseMinDist(String s) {
        return parseRadius(s);
    }

    /**
     * Parsees the world
     *
     * @param s the input string
     * @return the World object or null if not exist
     */
    public static World parseWorld(String s) {
        return Bukkit.getWorld(s);
    }

    /**
     * Parse the coords into a pair of doubles (x and z)
     *
     * @param s the input string
     * @return the pair or null if couldn't parse
     */
    public static SimplePair<Double, Double> parseCoords(String s) {
        String[] parts = s.split(",");
        if (parts.length != 2) {
            return null;
        }
        try {
            Double x = Double.parseDouble(parts[0]);
            Double z = Double.parseDouble(parts[1]);
            if (Math.abs(x) > MAX_DISTANCE || Math.abs(z) > MAX_DISTANCE) {
                return null;
            }
            return new SimplePair<Double, Double>(x, z);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Parses a list of players
     *
     * @param s the input string
     * @return A pair of List type String (the parsed player list) and List type String (the unparsable players)
     */
    public static SimplePair<List<String>, List<String>> parsePlayers(String s) {
        ArrayList<String> toBeScattered = new ArrayList<String>();
        ArrayList<String> failed = new ArrayList<String>();
        if ("*".equals(s)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                toBeScattered.add(p.getName());
            }
        } else {
            String[] players = s.split(" ");
            for (String player : players) {
                Player p = Bukkit.getPlayer(player);
                if (p == null) {
                    failed.add(player);
                    continue;
                }
                if (!toBeScattered.contains(p.getName())) {
                    toBeScattered.add(p.getName());
                }
            }
        }
        return new SimplePair<List<String>, List<String>>(toBeScattered, failed);
    }

    public static List<String> getTypesOutput() {
        AbstractList<String> output = new ArrayList<String>();
        List<AbstractScatterType> scatterTypes = ScatterManager.getScatterTypes();
        if (scatterTypes.isEmpty()) {
            output.add(ChatColor.RED + "No scatter types loaded!");
        } else {
            output.add(ChatColor.AQUA + "Showing " + scatterTypes.size() + " loaded scatter types:");
        }
        for (AbstractScatterType st : scatterTypes) {
            output.add(ChatColor.GREEN + st.getScatterID() + ChatColor.GRAY + " - " + st.getDescription());
        }
        return output;
    }

    public static void scatter(AbstractScatterType type, Boolean useTeams, Double radius, Double mindist, SimplePair<Double, Double> coords, World w, List<String> players, CommandSender sender) {
        ScatterParams sp = new ScatterParams(w.getName(), coords.getKey(), coords.getValue(), radius);
        sp.setMinDistance(mindist);
        List<String> allowedBlocks = ConfigHandler.getConfig(ConfigHandler.MAIN).getStringList(ConfigNodes.SCATTER_ALLOWED_BLOCKS);
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
}
