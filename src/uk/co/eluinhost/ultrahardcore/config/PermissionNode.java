package uk.co.eluinhost.ultrahardcore.config;

import org.bukkit.permissions.Permission;

import java.util.List;

public interface PermissionNode {

    /**
     * @return the parent node
     */
    PermissionNode getParent();

    /**
     * @return the name of this individual node
     */
    String getName();

    /**
     * @return the full permission name
     */
    String getFullName();

    String toString();

    /**
     * @param children the children nodes to parse and add
     */
    void addChildren(String[] children);

    /**
     * get the direct child of this node with the given name
     * @param child the name
     * @return the node or null if not found
     */
    PermissionNode getChild(String child);
}
