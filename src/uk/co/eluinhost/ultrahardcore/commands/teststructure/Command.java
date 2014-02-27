package uk.co.eluinhost.ultrahardcore.commands.teststructure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    /**
     * @return name of the command
     */
    String name();

    /**
     * @return all aliases of the command
     */
    String[] aliases() default {};

    /**
     * @return the type of sender to allow
     */
    SenderType[] allowedTypes();
}
