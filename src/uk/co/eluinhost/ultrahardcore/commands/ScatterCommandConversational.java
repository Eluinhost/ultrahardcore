package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.commands.inter.CommandCancelHandler;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.scatter.ScatterManager;
import uk.co.eluinhost.ultrahardcore.scatter.types.AbstractScatterType;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;
import uk.co.eluinhost.ultrahardcore.util.SimplePair;

import java.util.*;

public class ScatterCommandConversational implements UHCCommand {

    ConversationFactory cf = new ConversationFactory(UltraHardcore.getInstance())
            .withEscapeSequence("cancel")
            .withPrefix(new ConversationPrefix() {
                @Override
                public String getPrefix(ConversationContext conversationContext) {
                    return ChatColor.GOLD+"";
                }
            })
            .withTimeout(60)
            .withLocalEcho(false)
            .withFirstPrompt(new ScatterPrompt())
            .withModality(false)
            .addConversationAbandonedListener(new CommandCancelHandler());

    private static final String scatterSyntax = "'/scatter' for interactive mode OR /scatter typeID yes/no radius[:mindist] world:[x,z] */player1 player2 player3";

    @Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(command.getName().equals("scatter")){
			if(!sender.hasPermission(PermissionNodes.SCATTER_COMMAND)){
				sender.sendMessage(ChatColor.RED+"You don't have permission "+PermissionNodes.SCATTER_COMMAND);
				return true;
			}

            if(args.length == 0){
                if(sender instanceof Player || sender instanceof ConsoleCommandSender){
                    Map<Object,Object> initial = new HashMap<Object,Object>();
                    initial.put("SENDER",sender);
                    cf.withInitialSessionData(initial).buildConversation((Conversable) sender).begin();
                    return true;
                }else{
                    sender.sendMessage("Interactive mode can only be used by players/console");
                    return true;
                }
            }

            if(args.length == 1 && args[0].equalsIgnoreCase("default")){
                FileConfiguration config = ConfigHandler.getConfig(ConfigHandler.MAIN);
                args = new String[5];
                args[0] = config.getString(ConfigNodes.SCATTER_DEFAULT_TYPE);
                args[1] = ""+config.getString(ConfigNodes.SCATTER_DEFAULT_TEAMS);
                args[2] = config.getInt(ConfigNodes.SCATTER_DEFAULT_RADIUS)
                        +":"
                        +config.getInt(ConfigNodes.SCATTER_DEFAULT_MINRADIUS);
                args[3] = config.getString(ConfigNodes.SCATTER_DEFAULT_WORLD)
                        +":"
                        +config.getInt(ConfigNodes.SCATTER_DEFAULT_X)
                        +","
                        +config.getInt(ConfigNodes.SCATTER_DEFAULT_Z);
                args[4] = config.getString(ConfigNodes.SCATTER_DEFAULT_PLAYERS);
            }

			/*
			 * Get the types of scatter available
			 */
            if(args.length == 1 && args[0].equalsIgnoreCase("types")){
                List<String> scatterTypeOutput = ScatterPrompt.getTypesOutput();
                for(String s : scatterTypeOutput){
                    sender.sendMessage(s);
                }
                return true;
            }

			/*
			 * Check sane
			 */
            if(args.length < 5){
                sender.sendMessage(ChatColor.RED+"Syntax: "+scatterSyntax);
                return true;
            }


			/*
			 * Get the list of people to be scattered
			 */
            StringBuilder sb = new StringBuilder();
            for(int i = 4; i <args.length;i++){
                sb.append(args[i]);
                sb.append(" ");
            }
            String player_list = sb.toString().trim();
            SimplePair<List<String>, List<String>> parsed_list = ScatterPrompt.parsePlayers(player_list);
            if(parsed_list.getValue().size() != 0){
                sender.sendMessage(ChatColor.RED+"Couldn't find the players "+Arrays.toString(parsed_list.getValue().toArray()));
            }
            if(parsed_list.getKey().size() == 0){
                sender.sendMessage(ChatColor.RED+"There are no players to scatter!");
                return true;
            }else{
                sender.sendMessage(ChatColor.AQUA+"Will scatter players: "+ Arrays.toString(parsed_list.getKey().toArray()));
            }

			/*
			 * get the world info and centre coords
			 */
            SimplePair<Double,Double> coords;
            String[] parts = args[3].split(":");
            World w = ScatterPrompt.parseWorld(parts[0]);
            if(w == null){
                sender.sendMessage(ChatColor.RED+"World "+parts[0]+" not found!");
                return true;
            }
            if(parts.length == 2){
                coords = ScatterPrompt.parseCoords(parts[1]);
                if(coords == null){
                    sender.sendMessage(ChatColor.RED+"The coords in "+args[3]+" are not recognized, use the format worldname:x,z");
                    return true;
                }
            }else{
                coords = new SimplePair<Double,Double>(w.getSpawnLocation().getX(), w.getSpawnLocation().getZ());
            }


			/*
			 * get radius and min distance
			 */
            Double mindist;
            Double radius;
            String[] radiusparts = args[2].split(":");
            if(radiusparts.length == 2){
                mindist = ScatterPrompt.parseMinDist(radiusparts[1]);
                if(mindist == null){
                    sender.sendMessage(ChatColor.RED+"Minimum distance in "+args[2]+" not detected as a number!");
                    return true;
                }
            }else{
                mindist = 0d;
            }
            radius = ScatterPrompt.parseRadius(radiusparts[0]);
            if(radius == null){
                sender.sendMessage(ChatColor.RED+"Radius in "+args[2]+" not detected as a number!");
                return true;
            }

			/*
			 * get the type of the scatter to do
			 */

            AbstractScatterType type = ScatterPrompt.parseScatterType(args[0]);
            if(type == null){
                sender.sendMessage(ChatColor.RED+"Scatter type "+args[0]+" not found. Type /scatter types to view the list of types");
                return true;
            }

			/*
			 * get whether to scatter in teams or not
			 */

            Boolean team_scatter = ScatterPrompt.parseTeams(args[1]);
            if(team_scatter == null){
                sender.sendMessage(ChatColor.RED+"I don't know what "+args[1]+" is. You must specify yes/no for teams.");
                return true;
            }

            ScatterPrompt.scatter(
                    type,
                    team_scatter,
                    radius,
                    mindist,
                    coords,
                    w,
                    parsed_list.getKey(),
                    sender);
			return true;
		}
		return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        ArrayList<String> r = new ArrayList<String>();
        if(args.length == 1){
            r.add("types");
            r.add("default");
            r.addAll(ScatterManager.getScatterTypeNames());
            return r;
        }
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("types")){
                return r;
            }
            r.add("yes");
            r.add("no");
            return r;
        }
        if(args.length == 3){
            r.add("radius:mindist");
            return r;
        }
        if(args.length == 4){
            return ServerUtil.getWorldNamesWithSpawn();
        }
        List<String> p = ServerUtil.getOnlinePlayers();
        p.add("*");
        return p;
    }

}
