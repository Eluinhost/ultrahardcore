package uk.co.eluinhost.ultrahardcore.commands.teststructure;

import org.bukkit.command.TabExecutor;

public interface CommandHandler extends TabExecutor {

    /**
     * Register all the commands for the given class
     * @param clazz the class to parse
     */
    void registerCommands(Class clazz);
}
