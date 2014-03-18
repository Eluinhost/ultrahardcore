package com.publicuhc.commands;

public interface CommandProxy {

    /**
     * Calls the command
     * @param request the request parameters
     */
    void callCommand(CommandRequest request);

    /**
     * @return the trigger to match
     */
    String getTrigger();

    /**
     * Add a child to this command
     * @param child the child to add
     */
    void addChild(CommandProxy child);

    /**
     * Remove a child with the trigger name
     * @param name the name to remove
     */
    void removeChild(String name);

    /**
     * Set the parent object
     * @param parent the parent to set to
     */
    void setParent(CommandProxy parent);

    /**
     * @return the full trigger name for the command
     */
    String getFullTrigger();

    /**
     * @param name the name to search for
     * @return object if found, null otherwise, returns null if name is null
     */
    CommandProxy getChild(String name);

    /**
     * @return the ID for this command
     */
    String getIdentifier();

    /**
     * Attempt to find the proxy with the given ID, checks down the tree
     * @param id the id to search for
     * @return the commandproxy if found or null otherwise
     */
    CommandProxy findIdentifier(String id);
}
