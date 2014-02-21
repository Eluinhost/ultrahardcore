package uk.co.eluinhost.ultrahardcore.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TimerCommand extends UHCCommand {


    private int jobID = -1;
    private final int ENTITY_ID = Short.MAX_VALUE-375;

    private ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    private PacketContainer spawnPacket = pm.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
    private PacketContainer destroyPacket = pm.createPacket(PacketType.Play.Server.ENTITY_DESTROY);

    public TimerCommand(){
        destroyPacket.getIntegerArrays().write(0,new int[]{ENTITY_ID});
        try {
            destroyTextBar();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(command.getName().equals("timer")){
            if(!sender.hasPermission(PermissionNodes.TIMER_COMMAND)){
                sender.sendMessage(ChatColor.RED+"You don't have permission to use this command");
                return true;
            }
            if(args.length >= 1 && args[0].equalsIgnoreCase("cancel")){
                if(jobID == -1){
                    sender.sendMessage(ChatColor.RED+"There is no timer running!");
                    return true;
                }
                stopTimer();
                sender.sendMessage(ChatColor.GOLD+"Timer stopped!");
                return true;
            }
            if(jobID != -1){
                sender.sendMessage(ChatColor.RED+"There is already a timer running! Cancel it with /timer cancel");
                return true;
            }
            if(args.length < 2){
                sender.sendMessage(ChatColor.RED+"Syntax: /timer duration message to send");
                return true;
            }
            int time;
            try{
                time = Integer.parseInt(args[0]);
            }catch (NumberFormatException ignored){
                sender.sendMessage(ChatColor.RED+args[0]+" is not a number!");
                return true;
            }
            StringBuilder sb = new StringBuilder();
            for(int i = 1; i<args.length;i++){
                sb.append(args[i]).append(" ");
            }
            startTimer(time * 2,sb.toString());
			return true;
		}
		return false;
	}

    private void stopTimer(){
        if(jobID != -1){
            Bukkit.getScheduler().cancelTask(jobID);
            jobID = -1;
        }
        try {
            destroyTextBar();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param ticks int, number of ticks / 10 to run for
     */
    public void startTimer(final int ticks,final String message){
        jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(),
                new Runnable(){

                    private int ticksLeft = ticks;

                    @Override
                    public void run() {
                        if(--ticksLeft == 0){
                            stopTimer();
                            return;
                        }
                        try {
                            displayTextBar(message+ticksToString(ticksLeft),((float)ticksLeft/(float)ticks)*200F);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                },0,10);
    }

    private String ticksToString(int ticks){
        String output = "";

        int hours = (int) Math.floor((double)ticks/7200D); //half seconds in a hour
        ticks = ticks - (hours * 7200);
        int minutes = (int) Math.floor((double)ticks/120D);    //half seconds in a minute
        ticks = ticks - (minutes * 120);
        int seconds = (int) Math.floor((double) ticks / 2D);

        if(hours > 0)
            output += (hours+"h");
        if(minutes>0)
            output += (minutes+"m");
        output+= (seconds+"s");

        return output;
    }

    public void destroyTextBar() throws InvocationTargetException {
        for(Player p : Bukkit.getOnlinePlayers()){
            pm.sendServerPacket(p,destroyPacket);
        }
    }

    public void displayTextBar(String text, float health) throws InvocationTargetException {
        for(Player player : Bukkit.getOnlinePlayers()){
            PacketContainer pc = spawnPacket.deepClone();
            pc.getIntegers()
                    .write(0, ENTITY_ID)
                    .write(1, (int) EntityType.ENDER_DRAGON.getTypeId())  //entity type ID
                    .write(2, (int) player.getLocation().getX() * 32)        //x
                    .write(3, -200 * 32)                                    //y
                    .write(4, (int) player.getLocation().getZ() * 32);       //z
            WrappedDataWatcher watcher = pc.getDataWatcherModifier().read(0);
            watcher.setObject(0,(byte)0x20);   //invisible
            watcher.setObject(6,health);   //health
            watcher.setObject(10, text.substring(0, Math.min(text.length(),64)));
            pm.sendServerPacket(player, pc);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<String>();
    }
}
