package uk.co.eluinhost.ultrahardcore.commands.teststructure;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import uk.co.eluinhost.ultrahardcore.commands.teststructure.exception.*;

import java.lang.reflect.Method;
import java.util.*;

public class CommandHandler implements TabExecutor {

    private final CommandMap m_commandMap = new CommandMap();

    /**
     * Stores a list of class
     */
    private final Map<String,Object> m_instances = new HashMap<String,Object>();

    private static final String COMMAND_NOT_FOUND = ChatColor.RED + "Couldn't find the command requested";

    private static final class CommandHandlerHolder {
        private static final CommandHandler COMMAND_HANDLER = new CommandHandler();
    }

    /**
     * @return instance of the bukkit command handler
     */
    public static final CommandHandler getInstance(){
        return CommandHandlerHolder.COMMAND_HANDLER;
    }

    /**
     * Create the bukkit command handler
     */
    private CommandHandler() {}

    /**
     * Register the commands within the class
     * @param clazz the class to check
     * @throws CommandCreateException if there is an error creating the instance for calling the commands
     * @throws CommandIDConflictException when an ID is already taken
     * @throws CommandParentNotFoundException when a parent ID doesn't point to anything valid
     */
    public void registerCommands(Class clazz) throws CommandCreateException, CommandIDConflictException, CommandParentNotFoundException, InvalidMethodParametersException {
        Object instance = getClassInstance(clazz.getName());
        if(instance == null){
            //noinspection OverlyBroadCatchBlock
            try {
                instance = clazz.getConstructor().newInstance();
                m_instances.put(clazz.getName(),instance);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CommandCreateException();
            }
        }

        Map<String, List<String>> parentsToChildren = new HashMap<String, List<String>>();
        Map<String,ICommandProxy> allCommands = new HashMap<String, ICommandProxy>();

        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods){
            Command methodAnnotation = method.getAnnotation(Command.class);
            if(methodAnnotation != null){
                //check if ID already exists
                if(m_commandMap.getCommandByIdentifier(methodAnnotation.identifier())!=null){
                    throw new CommandIDConflictException();
                }

                //check if the method has 1 parameter of type CommandRequest
                Class[] parameterTypes = method.getParameterTypes();
                if(parameterTypes.length != 1 || !parameterTypes[0].equals(CommandRequest.class)){
                    throw new InvalidMethodParametersException();
                }

                //Make the new command proxy object
                ICommandProxy commandProxy = new CommandProxy(method,instance,methodAnnotation.trigger(),methodAnnotation.identifier());

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

        //for all the parent->children mappings generate the trees for the parent we own and remove the children from the list
        for (Map.Entry<String, List<String>> entry : parentsToChildren.entrySet()) {
            //the parent ID
            String parentID = entry.getKey();
            //all the children we want to add to the parent
            List<String> children = entry.getValue();

            //if we have the parent
            if (allCommands.containsKey(parentID)) {
                //get the parent and add all the children
                ICommandProxy parent = allCommands.get(entry.getKey());
                for (String child : children) {
                    parent.addChild(allCommands.get(child));
                }
            }
            //else add the children to the existing map of commands
            else {
                for (String child : children) {
                    try {
                        m_commandMap.addCommand(allCommands.get(child), entry.getKey());
                    } catch (CommandNotFoundException e) {
                        e.printStackTrace();
                        //this should never happen if logic is correct
                        Bukkit.getLogger().severe("Error adding command to map, parent ID "+parentID+" not valid.");
                    }
                }
            }
        }
    }

    /**
     * Gets the instance stored for the class name
     * @param className the class name to check for
     * @return the class if exists or null otherwise
     */
    public Object getClassInstance(String className) {
        return m_instances.get(className);
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
            m_commandMap.callCommand(new CommandRequest(convertArgs(args),sender));
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