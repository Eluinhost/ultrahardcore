package uk.co.eluinhost.ultrahardcore.commands.teststructure;

import org.bukkit.command.TabExecutor;

public interface CommandHandler extends TabExecutor {

    /**
     * Register all the commands for the given class
     * @param clazz the class to parse
     */
    void registerCommands(Class clazz);

    /**
     * Returns the stored object for the given class or null if none stored
     * @param className the class name to check for
     * @return the stored instance
     */
    Object getClassInstance(String className);
}
