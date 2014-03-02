package uk.co.eluinhost.commands;

import org.bukkit.ChatColor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    /**
     * @return trigger of the command
     */
    String trigger();

    /**
     * @return unique ID for this command
     */
    String identifier();

    /**
     * @return The command's parent's ID or empty string if root command
     */
    String parentID() default "";

    /**
     * @return minimum amount of arguments allowed
     */
    int minArgs() default 0;

    /**
     * @return maximum amount of arguments allowed
     */
    int maxArgs() default -1;

    /**
     * @return array of SenderType allowed to run the command, default all allowed
     */
    SenderType[] senders() default {SenderType.COMMAND_BLOCK,SenderType.CONSOLE,SenderType.PLAYER,SenderType.REMOTE_CONSOLE};
}
