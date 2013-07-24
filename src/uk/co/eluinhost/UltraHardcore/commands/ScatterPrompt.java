package uk.co.eluinhost.UltraHardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.exceptions.MaxAttemptsReachedException;
import uk.co.eluinhost.UltraHardcore.exceptions.WorldNotFoundException;
import uk.co.eluinhost.UltraHardcore.scatter.PlayerTeleportMapping;
import uk.co.eluinhost.UltraHardcore.scatter.ScatterManager;
import uk.co.eluinhost.UltraHardcore.scatter.ScatterParams;
import uk.co.eluinhost.UltraHardcore.scatter.types.ScatterType;

import java.util.*;

public class ScatterPrompt extends ValidatingPrompt {

    private static final int MAX_DISTANCE = 2000000;

    enum DATA{
        TYPE,TEAMS,RADIUS,MINDIST,WORLD,COORDS,PLAYERS
    }

    private DATA getNextMissing(ConversationContext context){
        for(DATA d : DATA.values()){
            if(context.getSessionData(d) == null){
                return d;
            }
        }
        return null;
    }

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        DATA d = getNextMissing(conversationContext);
        switch(d){
            case TYPE:
                if((s.equalsIgnoreCase("types") || ScatterManager.getScatterType(s) != null)){
                    return true;
                }
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"Invalid type: "+s);
                return false;
            case TEAMS:
                if(s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("no")){
                    return true;
                }
                conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"You can only use yes/no here");
                return false;
            case RADIUS:
            case MINDIST:
                try{
                    Double d_d = Double.parseDouble(s);
                    if(d_d > MAX_DISTANCE || d_d < 0){
                        conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"Be sensible with the numbers!");
                        return false;
                    }
                    return true;
                }catch (Exception ex){
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED+s+" is not a number!");
                    return false;
                }
            case WORLD:
                World w = Bukkit.getWorld(s);
                if(w == null){
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"The world "+s+" doesn't exist!");
                    return false;
                }
                return true;
            case COORDS:
                String[] coords = s.split(",");
                if(coords.length != 2){
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"Coordinates need to be in the format x,z");
                    return false;
                }
                try{
                    Double d_x = Double.parseDouble(coords[0]);
                    Double d_z = Double.parseDouble(coords[1]);
                    if(d_x > MAX_DISTANCE || d_z > MAX_DISTANCE){
                        conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"Be sensible with the numbers!");
                        return false;
                    }
                    return true;
                }catch(Exception ex){
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED+s+" are not valid coords!");
                    return false;
                }
            case PLAYERS:
                if(s.equals("*")){
                    return true;
                }else{
                    ArrayList<Player> to_be_scattered = new ArrayList<Player>();
                    String[] parts = s.split(" ");
                    for(String player : parts){
                        Player p = Bukkit.getPlayer(player);
                        if(p == null){
                            conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"Player "+player+" does not exist!");
                            return false;
                        }
                        if(to_be_scattered.contains(p)){
                            continue;
                        }
                        to_be_scattered.add(p);
                    }
                    if(to_be_scattered.size() == 0){
                        conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"There are no valid players to scatter!");
                        return false;
                    }
                    return true;
                }
            default:
                return false;
        }
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        if(ScatterManager.isScatterInProgress()){
            conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"A scatter command has already been started, please wait until it completes and try again");
            return END_OF_CONVERSATION;
        }
        DATA data = getNextMissing(conversationContext);
        switch (data){
            case TYPE:
                if(s.equals("types")){
                    ArrayList<ScatterType> scatterTypes = ScatterManager.getScatterTypes();
                    if(scatterTypes.size() == 0){
                        conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"No scatter types loaded!");
                    }
                    for(ScatterType st : scatterTypes){
                        conversationContext.getForWhom().sendRawMessage(ChatColor.GREEN+st.getScatterName()+ChatColor.GRAY+" - "+st.getDescription());
                    }
                }else{
                    ScatterType st = ScatterManager.getScatterType(s);
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA+"Using the scatter type: "+st.getScatterName());
                    conversationContext.setSessionData(DATA.TYPE, st);
                }
                break;
            case TEAMS:
                conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA+"Will scatter players "+(s.equalsIgnoreCase("yes")?"using":" disregarding ")+" teams.");
                conversationContext.setSessionData(DATA.TEAMS, s.equalsIgnoreCase("yes"));
                break;
            case RADIUS:
                Double radius = Double.parseDouble(s);
                conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA+"Using radius: "+radius);
                conversationContext.setSessionData(DATA.RADIUS,radius);
                break;
            case MINDIST:
                Double mindist = Double.parseDouble(s);
                conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA+"Using minimum distance: "+mindist);
                conversationContext.setSessionData(DATA.MINDIST,mindist);
                break;
            case WORLD:
                World world = Bukkit.getWorld(s);
                conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA+"Using world: "+world.getName());
                conversationContext.setSessionData(DATA.WORLD,world);
                break;
            case COORDS:
                String[] coords_s = s.split(",");
                double[] coords_d = new double[]{Double.parseDouble(coords_s[0]), Double.parseDouble(coords_s[1])};
                conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA+"Using centre coords: "+coords_d[0]+","+coords_d[1]);
                conversationContext.setSessionData(DATA.COORDS,coords_d);
                break;
            case PLAYERS:
                ArrayList<String> to_be_scattered = new ArrayList<String>();
                if(s.equals("*")){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        to_be_scattered.add(p.getName());
                    }
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA+"Will scatter all currently online players");
                }else{
                    String[] players = s.split(" ");
                    for (String player : players) {
                        Player p = Bukkit.getPlayer(player);
                        if(p != null && !to_be_scattered.contains(p.getName())){
                            to_be_scattered.add(p.getName());
                        }
                    }
                    conversationContext.getForWhom().sendRawMessage(ChatColor.AQUA+"Will scatter players: "+ Arrays.toString(to_be_scattered.toArray()));
                }
                conversationContext.setSessionData(DATA.PLAYERS,to_be_scattered);
            default:
                World w = (World) conversationContext.getSessionData(DATA.WORLD);
                double[] c = (double[]) conversationContext.getSessionData(DATA.COORDS);
                double r = (Double) conversationContext.getSessionData(DATA.RADIUS);
                ScatterParams sp = new ScatterParams(w.getName(),c[0], c[1], r);
                sp.setMinDistance((Double) conversationContext.getSessionData(DATA.MINDIST));
                sp.setAllowedBlocks(ConfigHandler.getConfig(ConfigHandler.MAIN).getIntegerList(ConfigNodes.SCATTER_ALLOWED_BLOCKS));

                /*
                 * get the right amount of people to scatter
                 */
                @SuppressWarnings("unchecked")
                ArrayList<String> all_players_s = (ArrayList<String>)conversationContext.getSessionData(DATA.PLAYERS);

                HashMap<String,ArrayList<Player>> teams = new HashMap<String,ArrayList<Player>>();
                ArrayList<Player> noteams = new ArrayList<Player>();

                if((Boolean)conversationContext.getSessionData(DATA.TEAMS)){
                    Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
                    for(String p : all_players_s){
                        Player pl = Bukkit.getPlayer(p);
                        if(pl == null){
                            conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"The player "+p+" is now offline, will not include them for scattering");
                            continue;
                        }
                        Team t = sb.getPlayerTeam(pl);
                        if(t == null){
                            noteams.add(pl);
                        }else{
                            ArrayList<Player> team = teams.get(t.getName());
                            if(team == null){
                                team = new ArrayList<Player>();
                                teams.put(t.getName(),team);
                            }
                            team.add(pl);
                        }
                    }
                }else{
                    for(String p : all_players_s){
                        Player pl = Bukkit.getPlayer(p);
                        if(pl == null){
                            conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"The player "+p+" is now offline, will not include them for scattering");
                            continue;
                        }
                        noteams.add(pl);
                    }
                }

                int number_of_ports = noteams.size()+teams.keySet().size();

                List<Location> teleports;
                try {
                    teleports = ((ScatterType)conversationContext.getSessionData(DATA.TYPE)).getScatterLocations(sp, number_of_ports);
                } catch (WorldNotFoundException e) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED+"The world specified doesn't exist!");
                    return END_OF_CONVERSATION;
                } catch (MaxAttemptsReachedException e) {
                    conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Max scatter attempts reached and not all players have valid locations, cancelling scatter");
                    return END_OF_CONVERSATION;
                }
                Iterator<Location> teleport_iterator = teleports.iterator();
                ArrayList<PlayerTeleportMapping> ptms = new ArrayList<PlayerTeleportMapping>();
                for(Player p : noteams){
                    Location next = teleport_iterator.next();
                    ptms.add(new PlayerTeleportMapping(p.getName(),next,null));
                }
                for(String team_name : teams.keySet()){
                    Location next = teleport_iterator.next();
                    for(Player p : teams.get(team_name)){
                        ptms.add(new PlayerTeleportMapping(p.getName(),next,team_name));
                    }
                }
                ScatterManager.addTeleportMappings(ptms,conversationContext.getForWhom());
                return END_OF_CONVERSATION;
        }
        return new ScatterPrompt();
    }

    private static final String SUFFIX = "Wait 60 seconds or type 'cancel' to cancel this command.";
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        switch(getNextMissing(conversationContext)){
            case TYPE:
                return "Enter scatter type to use or 'types' for a list of types. "+SUFFIX;
            case TEAMS:
                return "Should players be scattered as teams? yes/no. "+SUFFIX;
            case RADIUS:
                return "Enter the radius people should be scattered within. "+SUFFIX;
            case MINDIST:
                return "Enter the minimum distance allowed between players/teams. "+SUFFIX;
            case WORLD:
                List<World> worlds = Bukkit.getWorlds();
                StringBuilder sb = new StringBuilder();
                sb.append("Enter the name of the world to scatter into. Loaded worlds:");
                for(World w : worlds){
                    sb.append(" ");
                    sb.append(w.getName());
                }
                sb.append(" ");
                sb.append(SUFFIX);
                return sb.toString();
            case COORDS:
                Location loc = ((World) conversationContext.getSessionData(DATA.WORLD)).getSpawnLocation();
                return "Enter the centre coords to use, chosen world spawn coordinates are: "
                        +loc.getX()+","+loc.getZ()+" "+SUFFIX;
            case PLAYERS:
                return "Enter the players to teleport separated by spaces or '*' to scatter all players "+SUFFIX;
        }
        return "ERROR IN COMMAND";
    }
}
