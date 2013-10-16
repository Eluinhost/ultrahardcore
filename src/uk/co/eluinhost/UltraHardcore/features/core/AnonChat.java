package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class AnonChat extends UHCFeature {
	
	public AnonChat(boolean enabled) {
		super(enabled);
		setFeatureID("AnonChat");
		setDescription("Allows players to chat without revealing their name");
	}
	
	private final String prefix = ChatColor.RESET+"<"+ChatColor.MAGIC+"SECRET"+ChatColor.RESET+">";
	
	@EventHandler
	public void onAsyncChatEvent(AsyncPlayerChatEvent apce){
		if(isEnabled()){
			final String[] tokens = apce.getMessage().split(" ");
			if(tokens.length > 0 && tokens[0].equals("P")){
				final String playerName = apce.getPlayer().getName();
				apce.setCancelled(true);
				Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(), new Runnable() {
				    @Override
					public void run() {
				    	Player p = Bukkit.getPlayer(playerName);
						if(p == null){
							return;
						}
				    	if(p.hasPermission("UHC.AnonChat.chat")){
							StringBuilder buf = new StringBuilder();
							for(int i = 1;i<tokens.length;i++){
								buf.append(" ");
								buf.append(tokens[i]);
							}
							String finalMessage = buf.toString();
							for(Player pl : Bukkit.getOnlinePlayers()){
								if(pl.hasPermission("UHC.AnonChat.seeName")){
									pl.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+playerName+prefix+ChatColor.RESET+finalMessage);
								}else{
									pl.sendMessage(prefix+finalMessage);
								}
								Bukkit.getLogger().info("[AnonChat]["+playerName+"]"+finalMessage);
							}
						}else{
							p.sendMessage(ChatColor.RED+"You don't have the permissions to use annonymous chat");
						}
					}
				 
				});
				
			}
		}
	}
	

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}	
}
