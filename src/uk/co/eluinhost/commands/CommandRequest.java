package uk.co.eluinhost.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CommandRequest {

    private final List<String> m_args;
    private final CommandSender m_sender;

    /**
     * @param args the arguements to use
     * @param sender the sender for the request
     */
    public CommandRequest(List<String> args, CommandSender sender) {
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
}
