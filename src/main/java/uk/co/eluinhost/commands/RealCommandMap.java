package uk.co.eluinhost.commands;

import uk.co.eluinhost.commands.exceptions.CommandNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

public class RealCommandMap implements CommandMap {

    private final Collection<CommandProxy> m_children = new ArrayList<CommandProxy>();

    @Override
    public void callCommand(CommandRequest request) throws CommandNotFoundException {
        //get the command name
        String name = request.getCommandName();

        //check for commands with the given name
        CommandProxy commandToRun = getChild(name);

        //if there isn't a command throw an error
        if(commandToRun == null){
            throw new CommandNotFoundException();
        }
        commandToRun.callCommand(request);
    }

    @Override
    public void addCommand(CommandProxy command, String parentID) throws CommandNotFoundException {
        if(parentID.isEmpty()){
            m_children.add(command);
            return;
        }

        CommandProxy parent = getCommandByIdentifier(parentID);
        if(null == parent){
            throw new CommandNotFoundException();
        }

        parent.addChild(command);
    }

    @Override
    public CommandProxy getCommandByIdentifier(String identifier){
        for(CommandProxy command : m_children){
            CommandProxy found = command.findIdentifier(identifier);
            if(found != null){
                return found;
            }
        }
        return null;
    }

    @Override
    public CommandProxy getChild(String name) {
        for(CommandProxy command : m_children){
            if(command.getTrigger().equals(name)){
                return command;
            }
        }
        return null;
    }
}