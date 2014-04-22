package com.publicuhc.commands;

import com.google.common.collect.MutableClassToInstanceMap;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Injector;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import com.publicuhc.commands.exceptions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class RealCommandHandler implements CommandHandler {

    private final CommandMap m_commandMap;

    /**
     * Stores a list of class
     */
    private final MutableClassToInstanceMap m_instances = MutableClassToInstanceMap.create();

    private static final String COMMAND_NOT_FOUND = ChatColor.RED + "Couldn't find the command requested";

    private final Logger m_logger;
    private final Injector m_injector;

    /**
     * Create the bukkit command handler
     * @param logger the logger to use
     * @param map the map to use
     * @param injector the injector
     */
    @Inject
    private RealCommandHandler(Logger logger, CommandMap map, Injector injector) {
        m_logger = logger;
        m_commandMap = map;
        m_injector = injector;
    }

    @Override
    public void setExecutor(String commandName) {
        PluginCommand pc = Bukkit.getPluginCommand(commandName);
        if (pc == null) {
            Bukkit.getLogger().warning("Plugin failed to register the command " + commandName + ", is the command already taken?");
        } else {
            pc.setExecutor(this);
            pc.setTabCompleter(this);
        }
    }

    @Override
    public void registerCommands(Class clazz) throws CommandCreateException, CommandIDConflictException, CommandParentNotFoundException, InvalidMethodParametersException {
        Object instance = getClassInstance(clazz);
        if(instance == null){
            //noinspection OverlyBroadCatchBlock
            try {
                instance = m_injector.getInstance(clazz);
                m_instances.putInstance(clazz,instance);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CommandCreateException();
            }
        }

        Map<String, List<String>> parentsToChildren = new HashMap<String, List<String>>();
        Map<String,CommandProxy> allCommands = new HashMap<String, CommandProxy>();

        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods){
            Command methodAnnotation = method.getAnnotation(Command.class);
            if(methodAnnotation != null){
                //check if ID already exists
                if(m_commandMap.getCommandByIdentifier(methodAnnotation.identifier())!=null){
                    m_logger.severe("Tried to reregister the command with the identifier " + methodAnnotation.identifier());
                    throw new CommandIDConflictException();
                }

                //check if the method has 1 parameter of type CommandRequest
                Class[] parameterTypes = method.getParameterTypes();
                if(parameterTypes.length != 1 || !parameterTypes[0].equals(CommandRequest.class)){
                    throw new InvalidMethodParametersException();
                }

                //Make the new command proxy object
                CommandProxy commandProxy = new RealCommandProxy(
                        method,
                        instance,
                        methodAnnotation
                );

                //get the list for the parent name
                List<String> children = parentsToChildren.get(methodAnnotation.parentID());
                if(children == null){
                    children = new ArrayList<String>();
                    parentsToChildren.put(methodAnnotation.parentID(), children);
                }

                //add the object to the list
                children.add(commandProxy.getIdentifier());
                allCommands.put(commandProxy.getIdentifier(), commandProxy);
            }
        }

        //commands don't have any conflicting IDs here, now we need to check parent IDs are all valid
        for(String parentname : parentsToChildren.keySet()){
            if(parentname.isEmpty()){
                continue;
            }
            if(!allCommands.containsKey(parentname) && m_commandMap.getCommandByIdentifier(parentname) == null){
                throw new CommandParentNotFoundException();
            }
        }

        //for all the parent->children mappings generate the trees for the parent
        for (Map.Entry<String, List<String>> entry : parentsToChildren.entrySet()) {
            //the parent ID
            String parentID = entry.getKey();
            //all the children we want to add to the parent
            List<String> children = entry.getValue();

            //if we have the parent
            if (allCommands.containsKey(parentID)) {
                //get the parent and add all the children
                CommandProxy parent = allCommands.get(entry.getKey());
                for (String child : children) {
                    parent.addChild(allCommands.get(child));
                }
            }
            //else add the children to the existing map of commands
            else {
                for (String child : children) {
                    try {
                        m_commandMap.addCommand(allCommands.get(child), entry.getKey());
                        if(entry.getKey().isEmpty()){
                            setExecutor(allCommands.get(child).getTrigger());
                        }
                        m_logger.info("Added command " + child + " with trigger " + allCommands.get(child).getTrigger() + " to map");
                    } catch (CommandNotFoundException e) {
                        e.printStackTrace();
                        //this should never happen if logic is correct
                        m_logger.severe("Error adding command to map, parent ID " + parentID + " not valid.");
                    }
                }
            }
        }
    }

    @Override
    public Object getClassInstance(Class className) {
        return m_instances.getInstance(className);
    }

    /**
     * Converts arguements with the " char to use 1 index
     * @param args the arguements to parse
     * @return array of converted strings
     */
    private static List<String> convertArgs(String[] args){
        List<String> finalArgs = new ArrayList<String>();
        for(int i = 0; i < args.length; i++){
            String arg = args[i];
            if(arg.charAt(0) == '"'){
                StringBuilder build = new StringBuilder();
                build.append(arg.substring(1));
                for(i += 1;i<args.length;i++){
                    build.append(" ");
                    String quotedArg = args[i];
                    if(quotedArg.charAt(quotedArg.length()-1) == '"'){
                        build.append(quotedArg.substring(0,quotedArg.length()-1));
                        break;
                    }else{
                        build.append(quotedArg);
                    }
                }
                finalArgs.add(build.toString());
            }else{
                finalArgs.add(arg);
            }
        }
        return finalArgs;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        try {
            m_commandMap.callCommand(new CommandRequest(command.getName(),convertArgs(args),sender));
        } catch (CommandNotFoundException ex) {
            ex.printStackTrace();
            sender.sendMessage(ChatColor.RED + COMMAND_NOT_FOUND);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        //TODO ... or not
        return null;
    }
}