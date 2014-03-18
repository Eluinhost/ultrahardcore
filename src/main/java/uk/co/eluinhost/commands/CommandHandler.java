package uk.co.eluinhost.commands;

import org.bukkit.command.TabExecutor;
import uk.co.eluinhost.commands.exceptions.*;

public interface CommandHandler extends TabExecutor{

    /**
     * Set the command name given's executor to our command handler
     * @param commandName the command name
     */
    void setExecutor(String commandName);

    /**
     * Register the commands within the class
     * @param clazz the class to check
     * @throws uk.co.eluinhost.commands.exceptions.CommandCreateException if there is an error creating the instance for calling the commands
     * @throws CommandIDConflictException when an ID is already taken
     * @throws CommandParentNotFoundException when a parent ID doesn't point to anything valid
     * @throws InvalidMethodParametersException when method doesn't have a single CommandRequest param
     */
    void registerCommands(Class clazz) throws CommandCreateException, CommandIDConflictException, CommandParentNotFoundException, InvalidMethodParametersException;

    /**
     * Gets the instance stored for the class name
     * @param className the class name to check for
     * @return the class if exists or null otherwise
     */
    Object getClassInstance(Class className);
}
