package com.publicuhc.commands;

import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class RealCommandProxy implements CommandProxy {

    private final Method m_method;
    private final Object m_instance;
    private CommandProxy m_parent;
    private final Collection<CommandProxy> m_children = new ArrayList<CommandProxy>();
    private final Command m_command;

    /**
     * Create a new commandproxy
     * @param method the method to call
     * @param instance the instance to call the method on
     * @param command the command annotation
     */
    public RealCommandProxy(Method method, Object instance, Command command) {
        m_method = method;
        m_instance = instance;
        m_command = command;
    }

    @Override
    public void callCommand(CommandRequest request) {
        //get the first argument
        String arg = request.getFirstArg();

        //check for child commands with the given name of the next argument given
        CommandProxy commandToRun = getChild(arg);

        //if there is a child make them handle it
        if(commandToRun != null){
            //remove the argument and pass on to child to handle
            request.removeFirstArg();
            commandToRun.callCommand(request);
            return;
        }

        if(!Arrays.asList(m_command.senders()).contains(request.getSenderType())){
            request.getSender().sendMessage(ChatColor.RED+"That command can't be ran from here!");
            return;
        }

        if(!m_command.permission().isEmpty() && !request.getSender().hasPermission(m_command.permission())){
            request.getSender().sendMessage(ChatColor.RED+"You don't have permission to use that. ("+m_command.permission()+")");
            return;
        }

        //check arguments length
        if(request.getArgs().size() < m_command.minArgs()){
            //TODO find usage for the command
            request.getSender().sendMessage(ChatColor.RED+"Not enough arguments supplied.");
            return;
        }
        if(m_command.maxArgs() != -1 && request.getArgs().size() > m_command.maxArgs()){
            //TODO find usage for the command
            request.getSender().sendMessage(ChatColor.RED+"Too many arguments supplied.");
            return;
        }

        //noinspection OverlyBroadCatchBlock
        try {
            m_method.invoke(m_instance,request);
        } catch (Exception e) {
            e.printStackTrace();
            request.getSender().sendMessage(ChatColor.RED+"Exception running command, please check the console for more information");
        }
    }

    @Override
    public String getTrigger() {
        return m_command.trigger();
    }

    @Override
    public void addChild(CommandProxy child) {
        m_children.add(child);
        child.setParent(this);
    }

    @Override
    public void removeChild(String name) {
        Iterator<CommandProxy> childIterator = m_children.iterator();
        while(childIterator.hasNext()){
            CommandProxy child = childIterator.next();
            if(child.getTrigger().equalsIgnoreCase(name)){
                childIterator.remove();
            }
        }
    }

    @Override
    public void setParent(CommandProxy parent) {
        //remove ourselves from the parent if we're getting a new one
        if(m_parent != null){
            m_parent.removeChild(m_command.trigger());
        }
        //set out parent
        m_parent = parent;
        //if it's not null add ourselves as a child
        if(parent != null && parent.getChild(m_command.trigger()) == null){
            parent.addChild(this);
        }
    }

    @Override
    public String getFullTrigger() {
        if(m_parent == null){
            return m_command.trigger();
        }
        return m_parent.getFullTrigger()+" "+m_command.trigger();
    }

    @Override
    public CommandProxy getChild(String name) {
        if(name == null){
            return null;
        }
        for(CommandProxy command : m_children){
            if(command.getTrigger().equalsIgnoreCase(name)){
                return command;
            }
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return m_command.identifier();
    }

    @Override
    public CommandProxy findIdentifier(String id) {
        if(m_command.identifier().equals(id)){
            return this;
        }
        for(CommandProxy command : m_children){
            CommandProxy found = command.findIdentifier(id);
            if(found != null){
                return found;
            }
        }
        return null;
    }
}
