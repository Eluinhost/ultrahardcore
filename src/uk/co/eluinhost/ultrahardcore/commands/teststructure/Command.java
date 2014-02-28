package uk.co.eluinhost.ultrahardcore.commands.teststructure;

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
}
