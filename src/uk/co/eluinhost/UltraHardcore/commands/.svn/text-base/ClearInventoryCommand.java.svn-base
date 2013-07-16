package uk.co.eluinhost.UltraHardcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.util.ServerUtil;

public class ClearInventoryCommand extends UHCCommand{

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(command.getName().equals("ci")){
			if(args.length == 0){
				if(!sender.hasPermission(PermissionNodes.CLEAR_INVENTORY_SELF)){
					sender.sendMessage(ChatColor.RED+"You don't have the permission "+PermissionNodes.CLEAR_INVENTORY_SELF);
					return true;
				}
				if(!(sender instanceof Player)){
					sender.sendMessage(ChatColor.RED+"You can only clear your own inventory as a player!");
					return true;
				}
				clearInventory((Player)sender);
				sender.sendMessage(ChatColor.GOLD+"Inventory cleared successfully");
				return true;
			}else{
				if(!sender.hasPermission(PermissionNodes.CLEAR_INVENTORY_OTHER)){
					sender.sendMessage(ChatColor.RED+"You don't have the permission "+PermissionNodes.CLEAR_INVENTORY_OTHER);
					return true;
				}
				ArrayList<String> not_found_names = new ArrayList<String>();
				if(args[0].equals("*")){
					for(Player p : Bukkit.getOnlinePlayers()){
						if(!p.hasPermission(PermissionNodes.CLEAR_INVENTORY_IMMUNE)){
							clearInventory(p);
						}
					}
					Bukkit.broadcastMessage(ChatColor.GOLD+"All player inventories cleared by "+sender.getName());				
					return true;
				}
				for(String pname : args){
					Player p = Bukkit.getPlayer(pname);
					if(p == null){
						not_found_names.add(pname);
						continue;
					}
					if(!p.hasPermission(PermissionNodes.CLEAR_INVENTORY_IMMUNE)){
						clearInventory(p);
						p.sendMessage(ChatColor.GOLD+"Your inventory was cleared by "+sender.getName());
					}else{
						sender.sendMessage(ChatColor.RED+"Player "+p.getName()+" is immune to inventory clears");
					}
				}
				sender.sendMessage(ChatColor.GOLD+"All inventories cleared successfully");
				if(not_found_names.size() > 0){
					String message = ChatColor.GOLD+"Players not found to clear:";
					for(String s : not_found_names){
						message += " "+s;
					}
					sender.sendMessage(message);
				}
				return true;
			}
		}
		return false;
	}

	private void clearInventory(Player p){
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String label, String[] args) {
		List<String> p = ServerUtil.getOnlinePlayers();
		p.add("*");
		return p;
	}
}
