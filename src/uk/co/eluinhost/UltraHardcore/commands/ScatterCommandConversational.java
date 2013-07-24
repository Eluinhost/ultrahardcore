package uk.co.eluinhost.UltraHardcore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.commands.inter.CommandCancelHandler;
import uk.co.eluinhost.UltraHardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;

import java.util.*;

public class ScatterCommandConversational extends UHCCommand {

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
            .addConversationAbandonedListener(new CommandCancelHandler());

    @Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(command.getName().equals("scatter")){
			if(!sender.hasPermission(PermissionNodes.SCATTER_COMMAND)){
				sender.sendMessage(ChatColor.RED+"You don't have permission "+PermissionNodes.SCATTER_COMMAND);
				return true;
			}
            if(sender instanceof Player || sender instanceof ConsoleCommandSender){
                cf.buildConversation((Conversable) sender).begin();
            }
			return true;
		}
		return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

}
