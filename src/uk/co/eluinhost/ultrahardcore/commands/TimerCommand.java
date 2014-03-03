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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

//TODO complete, more refactor
public class TimerCommand implements UHCCommand {

    public static final long SECONDS_PER_HOUR = 3600;
    public static final long SECONDS_PER_MINUTE = 60;
    public static final int Y_COORD = -200;
    public static final int Y_MULTIPLIER = 32;
    public static final int X_MULTIPLIER = 32;
    public static final int Z_MULTIPLIER = 32;
    public static final int INVISIBLE_FLAG = 0x20;
    public static final int MAX_STRING_LEGNTH = 64;

    public static final String TIMER_COMMAND = "UHC.timer";

    private int m_jobID = -1;
    private static final int ENTITY_ID = Short.MAX_VALUE - 375;

    private final ProtocolManager m_protocolManager = ProtocolLibrary.getProtocolManager();
    private final PacketContainer m_spawnPacket = m_protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
    private final PacketContainer m_destroyPacket = m_protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);

    public TimerCommand() {
        m_destroyPacket.getIntegerArrays().write(0, new int[]{ENTITY_ID});
        try {
            destroyTextBar();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if ("timer".equals(command.getName())) {
            if (!sender.hasPermission(TIMER_COMMAND)) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                return true;
            }
            if (args.length >= 1 && "cancel".equalsIgnoreCase(args[0])) {
                if (m_jobID == -1) {
                    sender.sendMessage(ChatColor.RED + "There is no timer running!");
                    return true;
                }
                stopTimer();
                sender.sendMessage(ChatColor.GOLD + "Timer stopped!");
                return true;
            }
            if (m_jobID != -1) {
                sender.sendMessage(ChatColor.RED + "There is already a timer running! Cancel it with /timer cancel");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Syntax: /timer duration message to send");
                return true;
            }
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + args[0] + " is not a number!");
                return true;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            startTimer(time * 2, sb.toString());
            return true;
        }
        return false;
    }

    private void stopTimer() {
        if (m_jobID != -1) {
            Bukkit.getScheduler().cancelTask(m_jobID);
            m_jobID = -1;
        }
        try {
            destroyTextBar();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ticks int, number of ticks / 10 to run for
     * TODO use a class for the runnable
     */
    private void startTimer(final int ticks, final String message) {
        m_jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(),
                new Runnable() {

                    private int m_ticksLeft = ticks;

                    @Override
                    public void run() {
                        --m_ticksLeft;
                        if (m_ticksLeft == 0) {
                            stopTimer();
                            return;
                        }
                        try {
                            displayTextBar(message + ticksToString(m_ticksLeft), m_ticksLeft / (float) ticks * 200.0F);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, 10);
    }

    private static String ticksToString(long ticks) {
        int hours = (int) Math.floor(ticks / (double)(SECONDS_PER_HOUR * 2)); //half seconds in a hour
        ticks -= hours * SECONDS_PER_HOUR * 2;
        int minutes = (int) Math.floor(ticks / (double)(SECONDS_PER_MINUTE * 2));    //half seconds in a minute
        ticks -= minutes * SECONDS_PER_MINUTE * 2;
        int seconds = (int) Math.floor(ticks / (double) 2);

        String output = "";
        if (hours > 0) {
            output += hours + "h";
        }
        if (minutes > 0) {
            output += minutes + "m";
        }
        output += seconds + "s";

        return output;
    }

    private void destroyTextBar() throws InvocationTargetException {
        for (Player p : Bukkit.getOnlinePlayers()) {
            m_protocolManager.sendServerPacket(p, m_destroyPacket);
        }
    }

    private void displayTextBar(String text, float health) throws InvocationTargetException {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketContainer pc = m_spawnPacket.deepClone();
            pc.getIntegers()
                    .write(0, ENTITY_ID)
                    .write(1, (int) EntityType.ENDER_DRAGON.getTypeId())  //entity type ID
                    .write(2, (int) player.getLocation().getX() * X_MULTIPLIER)        //x
                    .write(3, Y_COORD * Y_MULTIPLIER)                                    //y
                    .write(4, (int) player.getLocation().getZ() * Z_MULTIPLIER);       //z
            WrappedDataWatcher watcher = pc.getDataWatcherModifier().read(0);
            watcher.setObject(0, (byte) INVISIBLE_FLAG);   //invisible
            watcher.setObject(6, health);   //health
            watcher.setObject(10, text.substring(0, Math.min(text.length(), MAX_STRING_LEGNTH)));
            m_protocolManager.sendServerPacket(player, pc);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<String>();
    }
}
