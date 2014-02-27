package uk.co.eluinhost.ultrahardcore.commands.teststructure;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BukkitCommandHandler implements CommandHandler {

    /**
     * Stores a list of class
     */
    private final Map<String,Object> m_instances = new HashMap<String,Object>();

    @Override
    public void registerCommands(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods){
            Command methodAnnotation = method.getAnnotation(Command.class);
            if(methodAnnotation != null){
                try {
                    addCommand(clazz, method,methodAnnotation);
                } catch (Exception ignored) {
                    Bukkit.getLogger().severe("Error trying to process the command method "+method.getName()+" in the class "+clazz.getName());
                }
            }
        }
    }

    @Override
    public Object getClassInstance(String className) {
        return m_instances.get(className);
    }

    /**
     * Add the command to the command map, must be a valid class/method/annotation combo before calling
     * @param clazz The class to store it under
     * @param method the method to invoke
     * @param annotation the annotation to get details from
     */
    private void addCommand(Class clazz,Method method,Annotation annotation) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object storedInstance = getClassInstance(clazz.getName());
        if(null == storedInstance){
            storedInstance = clazz.getConstructor().newInstance();
            m_instances.put(clazz.getName(),storedInstance);
        }
        //TODO make an internal representation of the method for easy invokation later on using the annotation details
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        //TODO find the relevant command object
        //TODO check sender type
        //TODO generate a command request object
        //TODO invoke the method with the request
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        //TODO ... or not
        return null;
    }
}