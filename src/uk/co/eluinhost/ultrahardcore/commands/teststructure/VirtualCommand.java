package uk.co.eluinhost.ultrahardcore.commands.teststructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class VirtualCommand {

    private final Method m_method;
    private final Command m_command;
    private final String m_className;
    private final Collection<VirtualCommand> m_children = new LinkedList<VirtualCommand>();

    /**
     * A virtual command to represent a command method in a class
     * @param method the method to run
     * @param command the command annotation
     * @param className the class name to lookup the instance to invoke on
     */
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

    /**
     * Adds the child to this command
     * @param command the command to add
     */
    public void addChild(VirtualCommand command){
        m_children.add(command);
    }

    /**
     * Returns the command with the given ID, searches all children in the tree
     * @param id the ID to search for
     * @return the command if found, or null otherwise
     */
    public VirtualCommand getByID(String id){
        if(m_command.id().equals(id)){
            return this;
        }
        for(VirtualCommand childCommand : m_children){
            VirtualCommand command = childCommand.getByID(id);
            if(command != null){
                return command;
            }
        }
        return null;
    }

    /**
     * @return the command annotation
     */
    public Command getCommand(){
        return m_command;
    }
}
