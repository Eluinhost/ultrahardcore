package uk.co.eluinhost.commands;

import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CommandProxy implements ICommandProxy {

    private final Method m_method;
    private final Object m_instance;
    private final String m_trigger;
    private ICommandProxy m_parent = null;
    private final String m_identifier;
    private final Collection<ICommandProxy> m_children = new ArrayList<ICommandProxy>();

    /**
     * Create a new commandproxy
     * @param method the method to call
     * @param instance the instance to call the method on
     * @param trigger the trigger command
     * @param identifier the ID for this node
     */
    public CommandProxy(Method method, Object instance, String trigger, String identifier) {
        m_method = method;
        m_instance = instance;
        m_trigger = trigger;
        m_identifier = identifier;
    }

    @Override
    public void callCommand(CommandRequest request) {
        //get the first argument
        String arg = request.getFirstArg();

        //check for child commands with the given name
        ICommandProxy commandToRun = getChild(arg);

        //if there isn't a command handle it ourselves
        if(commandToRun == null){
            //noinspection OverlyBroadCatchBlock
            try {
                m_method.invoke(m_instance,request);
            } catch (Exception e) {
                e.printStackTrace();
                request.getSender().sendMessage(ChatColor.RED+"Exception running command, please check the console for more information");
            }
        }else{
            //remove the argument and pass on to child to handle
            request.removeFirstArg();
            commandToRun.callCommand(request);
        }
    }

    @Override
    public String getTrigger() {
        return m_trigger;
    }

    @Override
    public void addChild(ICommandProxy child) {
        m_children.add(child);
        child.setParent(this);
    }

    @Override
    public void removeChild(String name) {
        Iterator<ICommandProxy> childIterator = m_children.iterator();
        while(childIterator.hasNext()){
            ICommandProxy child = childIterator.next();
            if(child.getTrigger().equalsIgnoreCase(name)){
                childIterator.remove();
            }
        }
    }

    @Override
    public void setParent(ICommandProxy parent) {
        //remove ourselves from the parent if we're getting a new one
        if(m_parent != null){
            m_parent.removeChild(m_trigger);
        }
        //set out parent
        m_parent = parent;
        //if it's not null add ourselves as a child
        if(parent != null && parent.getChild(m_trigger) == null){
            parent.addChild(this);
        }
    }

    @Override
    public String getFullTrigger() {
        if(m_parent == null){
            return m_trigger;
        }
        return m_parent.getFullTrigger()+" "+m_trigger;
    }

    @Override
    public ICommandProxy getChild(String name) {
        if(name == null){
            return null;
        }
        for(ICommandProxy command : m_children){
            if(command.getTrigger().equalsIgnoreCase(name)){
                return command;
            }
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return m_identifier;
    }

    @Override
    public ICommandProxy findIdentifier(String id) {
        if(m_identifier.equals(id)){
            return this;
        }
        for(ICommandProxy command : m_children){
            ICommandProxy found = command.findIdentifier(id);
            if(found != null){
                return found;
            }
        }
        return null;
    }
}
