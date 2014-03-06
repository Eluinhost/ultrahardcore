package uk.co.eluinhost.commands;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.eluinhost.ultrahardcore.util.WordsUtil;

import java.util.Collections;
import java.util.List;

public class CommandRequest {

    private final List<String> m_args;
    private final CommandSender m_sender;
    private final String m_commandName;

    /**
     * @param commandName the commands name
     * @param args the arguements to use
     * @param sender the sender for the request
     */
    public CommandRequest(String commandName, List<String> args, CommandSender sender) {
        m_commandName = commandName;
        m_args = args;
        m_sender = sender;
    }

    /**
     * Remove the first element of the arguments
     */
    public void removeFirstArg(){
        if(!m_args.isEmpty()){
            m_args.remove(0);
        }
    }

    /**
     * @return unmodifiable list of the arguments supplied
     */
    public List<String> getArgs(){
        return Collections.unmodifiableList(m_args);
    }

    /**
     * @return the sender involved
     */
    public CommandSender getSender(){
        return m_sender;
    }

    /**
     * @return null if no args left or the arguement if exists
     */
    public String getFirstArg(){
        if(m_args.isEmpty()){
            return null;
        }
        return m_args.get(0);
    }

    /**
     * @return the last arg or null if not exist
     */
    public String getLastArg(){
        if(m_args.isEmpty()){
            return null;
        }
        return m_args.get(m_args.size()-1);
    }

    /**
     * @return the commands name
     */
    public String getCommandName() {
        return m_commandName;
    }

    /**
     * @return the type of the sender
     */
    public SenderType getSenderType(){
        return SenderType.getFromCommandSender(m_sender);
    }

    /**
     * @param index the index to look for
     * @return true if arg is an int, false otherwise
     */
    public boolean isArgInt(int index){
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(m_args.get(index));
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    /**
     * Get the int at the specified index
     * @param index the index to look for
     * @return -1 if not an int, int value otherwise
     */
    public int getInt(int index){
        int returnValue = -1;
        try{
            returnValue = Integer.parseInt(m_args.get(index));
        }catch (NumberFormatException ignored){}
        return returnValue;
    }

    /**
     * @param index the index to look for
     * @return null if not valid, world if valid
     */
    public World getWorld(int index){
        return Bukkit.getWorld(m_args.get(index));
    }

    /**
     * @param message message to pass on to the sender
     */
    public void sendMessage(String message){
        m_sender.sendMessage(message);
    }

    /**
     * @param index the index to look for
     * @return the argument
     */
    public String getArg(int index){
        return m_args.get(index);
    }

    /**
     * @param index the index to look for
     * @return true if within list bounds, false otherwise
     */
    public boolean isArgPresent(int index){
        return index > -1 && index < m_args.size();
    }

    /**
     * @param index the index to look for
     * @return the duration in millis
     */
    public long parseDuration(int index){
        return WordsUtil.parseTime(m_args.get(index));
    }

    /**
     * @param index the index to look for
     * @return player or null of not exists
     */
    public Player getPlayer(int index){
        return Bukkit.getPlayer(m_args.get(index));
    }

    /**
     * @param index the index to look for
     * @return true if arg is an number, false otherwise
     */
    public boolean isArgNumber(int index){
        return NumberUtils.isNumber(m_args.get(index));
    }

    /**
     * Get the Number at the specified index
     * @param index the index to look for
     * @return number
     * */
    public Number getNumber(int index){
        return NumberUtils.createNumber(m_args.get(index));
    }

    /**
     * Is the index a boolean value, 'true', 'on' or 'yes' count as true and 'false', 'off' or 'no' count as false, case insensitive
     * @param index the index to look for
     * @return true if boolean false otherwise
     */
    public boolean isArgBoolean(int index){
        return BooleanUtils.toBooleanObject(m_args.get(index)) != null;
    }

    /**
     * @param index the index to look for
     * @return the boolean value at that index
     */
    public boolean getBoolean(int index){
        return BooleanUtils.toBoolean(m_args.get(index));
    }
}
