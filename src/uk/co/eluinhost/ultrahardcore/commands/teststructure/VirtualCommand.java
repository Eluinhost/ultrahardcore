package uk.co.eluinhost.ultrahardcore.commands.teststructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class VirtualCommand {

    private final Method m_method;
    private final Command m_command;
    private final String m_className;

    public VirtualCommand(Method method, Command command, String className) {
        m_method = method;
        m_command = command;
        m_className = className;
    }

    /**
     * Is the sender type allowed for this command
     * @param type the type to check
     * @return true if allowed, false if not
     */
    public boolean isSenderAllowed(SenderType type){
        return Arrays.asList(m_command.allowedTypes()).contains(type);
    }

    /**
     * Runs the command reprensented by this
     *
     * @param request the request parameters
     * @throws IllegalAccessException - if this Method object is enforcing Java language access control and the underlying method is inaccessible.
     * @throws IllegalArgumentException - if the method is an instance method and the specified object argument is not an instance of the class or interface declaring the underlying method (or of a subclass or implementor thereof); if the number of actual and formal parameters differ; if an unwrapping conversion for primitive arguments fails; or if, after possible unwrapping, a parameter value cannot be converted to the corresponding formal parameter type by a method invocation conversion.
     * @throws InvocationTargetException - if the underlying method throws an exception.
     * @throws NullPointerException - if the specified object is null and the method is an instance method.
     * @throws ExceptionInInitializerError - if the initialization provoked by this method fails.
     */
    public void runCommand(CommandRequest request) throws InvocationTargetException, IllegalAccessException {
        m_method.invoke(/*TODO get the object for this command*/ null,request);
    }
}
