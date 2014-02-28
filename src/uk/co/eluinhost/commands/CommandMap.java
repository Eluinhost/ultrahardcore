package uk.co.eluinhost.commands;

import uk.co.eluinhost.commands.exceptions.CommandNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

public class CommandMap {

    private final Collection<ICommandProxy> m_children = new ArrayList<ICommandProxy>();

    /**
     * Start the command request chain
     * @param request the request to use
     * @throws CommandNotFoundException if the root command doesn't map to anything
     */
    public void callCommand(CommandRequest request) throws CommandNotFoundException {
        //get the first argument
        String arg = request.getFirstArg();

        //check for commands with the given name
        ICommandProxy commandToRun = getChild(arg);

        //if there isn't a command throw an error
        if(commandToRun == null){
            throw new CommandNotFoundException();
        }
        commandToRun.callCommand(request);
    }

    /**
     * Adds the command to the tree for the given parent ID
     * If the parentID is empty, adds as a root command
     * If the parentID doesn't exist, adds to the list of orphaned commands
     * @param command the command to add
     * @param parentID the command's parent ID
     */
    public void addCommand(ICommandProxy command, String parentID) throws CommandNotFoundException {
        if(parentID.isEmpty()){
            m_children.add(command);
            return;
        }

        ICommandProxy parent = getCommandByIdentifier(parentID);
        if(null == parent){
            throw new CommandNotFoundException();
        }

        parent.addChild(command);
    }

    /**
     * Gets the command anywhere in the tree with the given identifier
     * @param identifier the ID to search for
     * @return the command if found, null otherwise
     */
    public ICommandProxy getCommandByIdentifier(String identifier){
        for(ICommandProxy command : m_children){
            ICommandProxy found = command.findIdentifier(identifier);
            if(found != null){
                return found;
            }
        }
        return null;
    }

    /**
     * Gets the root level command with the given name
     * @param name the name to search for
     * @return the command if exists, null otherwise
     */
    private ICommandProxy getChild(String name) {
        for(ICommandProxy command : m_children){
            if(command.getIdentifier().equals(name)){
                return command;
            }
        }
        return null;
    }
}