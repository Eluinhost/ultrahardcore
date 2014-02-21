package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.features.FeatureManager;
import uk.co.eluinhost.ultrahardcore.features.core.DeathBansFeature;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;

public class DeathBanCommand extends UHCCommand {

    private final String SYNTAX = ChatColor.RED+"Syntax: /deathban ban <playername> <time> OR /deathban unban <playername>";
    private final String BAN_SYNTAX = ChatColor.RED+"Syntax: /deathban ban <playername> [time]";
    private final String UNBAN_SYNTAX = ChatColor.RED+"Syntax: /deathban unban <playername>";

    @Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(command.getName().equals("deathban")){
            if(args.length < 1){
                sender.sendMessage(SYNTAX);
                return true;
            }
            DeathBansFeature dbf;
            try {
                dbf = (DeathBansFeature) FeatureManager.getFeature("DeathBans");
            } catch (FeatureIDNotFoundException e) {
                sender.sendMessage(ChatColor.RED+"Module DeathBans is not loaded!");
                return true;
            }
            if(args[0].equalsIgnoreCase("ban"))  {
                if(!sender.hasPermission(PermissionNodes.DEATH_BAN_BAN)){
                    sender.sendMessage(ChatColor.RED+"You don't have permission");
                    return true;
                }
                if(args.length != 3){
                    sender.sendMessage(BAN_SYNTAX);
                    return true;
                }
                String playername = args[1];
                String time = args[2];
                long duration = DeathBansFeature.parseBanTime(time);
                dbf.banPlayer(Bukkit.getOfflinePlayer(playername),"You are under a death ban, you will be unbanned in %timeleft",duration);
                sender.sendMessage(ChatColor.GOLD+"Banned player "+playername+" for "+DeathBansFeature.formatTimeLeft(System.currentTimeMillis()+duration));
                return true;
            }else if(args[0].equalsIgnoreCase("unban")){
                if(!sender.hasPermission(PermissionNodes.DEATH_BAN_UNBAN)){
                    sender.sendMessage(ChatColor.RED+"You don't have permission");
                    return true;
                }
                if(args.length != 2){
                    sender.sendMessage(UNBAN_SYNTAX);
                    return true;
                }
                String playername = args[1];
                int amount = dbf.removeBan(playername);
                sender.sendMessage(ChatColor.GOLD+"Removed "+amount+" bans for player "+playername);
                return true;
            }else{
                sender.sendMessage(SYNTAX);
                return true;
            }
		}
		return false;
	}

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> r = new ArrayList<String>();
        if(args.length == 1){
            r.add("ban");
            r.add("unban");
            return r;
        }
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("ban")||args[0].equalsIgnoreCase("unban")){
                r.addAll(ServerUtil.getOnlinePlayers());
                return r;
            }
            return r;
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("ban")){
            r.add("2h30m");
            return r;
        }
        return r;
    }
}
