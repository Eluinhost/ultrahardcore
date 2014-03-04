package uk.co.eluinhost.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

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
}
