package uk.co.eluinhost.commands;

import uk.co.eluinhost.commands.exceptions.CommandNotFoundException;

public interface CommandMap {

    /**
     * Start the command request chain
     * @param request the request to use
     * @throws CommandNotFoundException if the root command doesn't map to anything
     */
    void callCommand(CommandRequest request) throws CommandNotFoundException;

    /**
     * Adds the command to the tree for the given parent ID
     * If the parentID is empty, adds as a root command
     * If the parentID doesn't exist, adds to the list of orphaned commands
     * @param command the command to add
     * @param parentID the command's parent ID
     * @throws CommandNotFoundException when parent is specified but not found
     */
    void addCommand(CommandProxy command, String parentID) throws CommandNotFoundException;

    /**
     * Gets the command anywhere in the tree with the given identifier
     * @param identifier the ID to search for
     * @return the command if found, null otherwise
     */
    CommandProxy getCommandByIdentifier(String identifier);

    /**
     * Gets the root level command with the given name
     * @param name the name to search for
     * @return the command if exists, null otherwise
     */
    CommandProxy getChild(String name);
}
