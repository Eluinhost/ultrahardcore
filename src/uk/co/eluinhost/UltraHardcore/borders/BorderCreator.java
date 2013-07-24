package uk.co.eluinhost.UltraHardcore.borders;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.eluinhost.UltraHardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.exceptions.BorderTypeNotFoundException;
import uk.co.eluinhost.UltraHardcore.exceptions.WorldEditMaxChangedBlocksException;
import uk.co.eluinhost.UltraHardcore.exceptions.WorldEditNotFoundException;
import uk.co.eluinhost.UltraHardcore.exceptions.WorldNotFoundException;
import uk.co.eluinhost.UltraHardcore.util.ServerUtil;

/**
 * Class to generate a border using worldedit, actual worldedit references are in WorldEditBorderCreator
 * @author ghowden
 *
 */
public class BorderCreator extends UHCCommand{
	
	private static boolean worldEditFound = false;
	
	//Generate a border that is of the params
	public static void generateBorder(BorderParams bp) throws WorldEditNotFoundException, WorldEditMaxChangedBlocksException, WorldNotFoundException, BorderTypeNotFoundException{
		if(worldEditFound){
			WorldEditBorderCreator.build(bp);
		}else{
			throw new WorldEditNotFoundException();
		}
	}
	
	public BorderCreator(){
		worldEditFound = Bukkit.getPluginManager().getPlugin("WorldEdit") != null;
		if(worldEditFound){
			WorldEditBorderCreator.initialize();
		}
	}
	public static boolean undoForWorld(String world)throws WorldEditNotFoundException, WorldNotFoundException{
		if(worldEditFound){
			return WorldEditBorderCreator.undoForWorld(world);
		}else{
			throw new WorldEditNotFoundException();
		}
	}

	private static final String syntax = "/generateborder radius world[:x,z] typeID[:blockid:meta] OR /generateborder undo/types [world]";
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(command.getName().equals("generateborder")){
			if(!sender.hasPermission(PermissionNodes.GENERATE_BORDER)){
				sender.sendMessage(ChatColor.RED+"You don't have permission "+PermissionNodes.GENERATE_BORDER);
				return true;
			}
			if(args.length >= 1){
				if(args[0].equalsIgnoreCase("undo")) try {
                    String world;
                    if (args.length != 1) {
                        world = args[1];
                    } else {
                        if (sender instanceof Player) {
                            world = ((Player) sender).getWorld().getName();
                        } else {
                            sender.sendMessage("You need to specify a world to undo using the console");
                            return true;
                        }
                    }
                    if (undoForWorld(world)) {
                        sender.sendMessage(ChatColor.GOLD + "Undone successfully!");
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Nothing left to undo!");
                    }
                    return true;
                } catch (WorldEditNotFoundException e) {
                    sender.sendMessage(ChatColor.RED + "WorldEdit " + args[1] + " not found, required to make borders!");
                    return true;
                } catch (WorldNotFoundException e) {
                    sender.sendMessage(ChatColor.RED + "World " + args[1] + " was not found!");
                    return true;
                }
                else if(args[0].equals("types")){
					if(worldEditFound){
						ArrayList<WorldEditBorder> types = WorldEditBorderCreator.getTypes();
						if(types.size() == 0){
							sender.sendMessage(ChatColor.RED+"No border types loaded!");
							return true;
						}else{
							sender.sendMessage(ChatColor.GOLD+"Loaded border types: ("+types.size()+")");
						}
						for(WorldEditBorder w : WorldEditBorderCreator.getTypes()){
							sender.sendMessage(ChatColor.GRAY+w.getID()+" - "+w.getDescription());
						}
						return true;
					}else{
						sender.sendMessage(ChatColor.RED+"World edit was not found! It is needed to use border functions");
						return true;
					}
				}
			}
			if(args.length != 3){
				sender.sendMessage(ChatColor.RED+"Invalid syntax: "+syntax);
				return true;
			}
			int radius;
			try{
				radius = Integer.parseInt(args[0]);
			}catch(Exception ex){
				sender.sendMessage(ChatColor.RED+"Unknown radius size: "+args[0]);
				return true;
			}
			int x ;
			int z ;
			World w ;
			if(args[1].contains(":")){
				String[] parts = args[1].split(":");
				if(parts.length != 2){
					sender.sendMessage(ChatColor.RED+"Invalid world name/coordinates, syntax for world is worldname:x,z");
					return true;
				}
				String[] parts2 = parts[1].split(",");
				if(parts2.length != 2){
					sender.sendMessage(ChatColor.RED+"Invalid world name/coordinates, syntax for world is worldname:x,z");
					return true;
				}
				try{
					x = Integer.parseInt(parts2[0]);
					z = Integer.parseInt(parts2[1]);
					args[1] = parts[0];
				}catch(Exception ex){
					sender.sendMessage(ChatColor.RED+"One or more world coordinates not a number, world syntax is worldname:x,z");
					return true;
				}
				w = Bukkit.getWorld(args[1]);
				if(w == null){
					sender.sendMessage(ChatColor.RED+"World "+args[1]+" not found!");
					return true;
				}
			}else{
				w = Bukkit.getWorld(args[1]);
				if(w == null){
					sender.sendMessage(ChatColor.RED+"World "+args[1]+" not found!");
					return true;
				}
				x = w.getSpawnLocation().getBlockX();
				z = w.getSpawnLocation().getBlockZ();
			}
			int borderID ;
			int metaID ;
			
			String[] blockinfo ;
			if(args[2].contains(":")){
				String[] blockinfos = args[2].split(":");
				if(blockinfos.length != 3){
					sender.sendMessage(ChatColor.RED+"Unknown block ID and meta, syntax: "+syntax);
					return true;
				}
				blockinfo = blockinfos;
			}else{
				blockinfo = new String[]{
						args[2],
						ConfigHandler.getConfig(ConfigHandler.MAIN).getString(ConfigNodes.BORDER_BLOCK),
						ConfigHandler.getConfig(ConfigHandler.MAIN).getString(ConfigNodes.BORDER_BLOCK_META)
				};
			}
			try{
				borderID = Integer.parseInt(blockinfo[1]);
			}catch(Exception ex){
				sender.sendMessage(ChatColor.RED+"Unknown number "+blockinfo[1]+" for block ID");
				return true;
			}
			try{
				metaID = Integer.parseInt(blockinfo[2]);
			}catch(Exception ex){
				sender.sendMessage(ChatColor.RED+"Unknown number "+blockinfo[2]+" for block meta");
				return true;
			}
						
			BorderParams bp = new BorderParams(x, z, radius, blockinfo[0], w.getName(), borderID, metaID);
			try {
				BorderCreator.generateBorder(bp);				
			} catch (WorldEditNotFoundException e) {
				sender.sendMessage(ChatColor.RED+"WorldEdit not found, required to make borders!");
				return true;
			} catch (WorldEditMaxChangedBlocksException e) {
				sender.sendMessage(ChatColor.RED+"Error, hit max changable blocks by WorldEdit");
				return true;
			} catch (WorldNotFoundException e) {
				sender.sendMessage(ChatColor.RED+"World "+args[1]+" not found!");
				return true;
			} catch (BorderTypeNotFoundException e) {
				sender.sendMessage(ChatColor.RED+"The border type "+blockinfo[0]+" was not found!, use /generateborder types to view all types");
				return true;
			}
			sender.sendMessage(ChatColor.GOLD+"World border created successfully");
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		ArrayList<String> r = new ArrayList<String>();
		if(args.length == 1){
			r.add("radius");
			r.add("undo");
			r.add("types");
			return r;
		}
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("undo")){
				return ServerUtil.getWorldNames();
			}else if(args[0].equalsIgnoreCase("types")){
				return r;
			}
			return ServerUtil.getWorldNamesWithSpawn();
		}
		if(args.length == 3){
			return WorldEditBorderCreator.getBorderIDs();
		}
		return r;
	}
}
