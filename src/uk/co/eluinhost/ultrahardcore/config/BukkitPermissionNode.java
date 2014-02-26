package uk.co.eluinhost.ultrahardcore.config;

import java.util.Collection;
import java.util.LinkedList;

public class BukkitPermissionNode implements PermissionNode {

    private final String m_name;
    private PermissionNode m_parent = null;
    private final Collection<PermissionNode> m_children = new LinkedList<PermissionNode>();

    /**
     * Make a node with a parent node with the given name
     * @param parent the parent node
     * @param name the name of this node
     */
    public BukkitPermissionNode(String name, PermissionNode parent){
        m_parent = parent;
        m_name = name;
    }

    /**
     * Make a node without a parent node with the given name
     * @param name the name of this node
     */
    public BukkitPermissionNode(String name){
        m_name = name;
    }

    /**
     * Splits a string around the '.' character
     * @param name the string to split
     * @return the array
     */
    private static String[] splitIntoParts(String name){
        return name.split("\\.");
    }

    @Override
    public PermissionNode getParent(){
        return m_parent;
    }

    @Override
    public String getName(){
        return m_name;
    }

    @Override
    public String getFullName(){
        return m_parent == null ? getName() : m_parent.getFullName() + "."+getName();
    }

    @Override
    public String toString(){
        return getFullName();
    }

    @SuppressWarnings("ProblematicVarargsMethodOverride")
    @Override
    public void addChildren(String... children) {
        for(String child : children){
            String[] parts = splitIntoParts(child);

            PermissionNode node = getChild(parts[0]);
            if(null == node){
                node = new BukkitPermissionNode(parts[0],this);
                m_children.add(node);
            }

            if(parts.length > 1){
                String[] subparts = new String[parts.length-1];
                System.arraycopy(parts,1,subparts,0,subparts.length);
                node.addChildren(subparts);
            }
        }
    }

    @Override
    public PermissionNode getChild(String child) {
        for(PermissionNode node : m_children){
            if(node.getName().equals(child)){
                return node;
            }
        }
        return null;
    }
}
