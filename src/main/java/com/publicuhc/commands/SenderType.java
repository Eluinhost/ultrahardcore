package com.publicuhc.commands;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public enum SenderType {
    COMMAND_BLOCK(BlockCommandSender.class),
    REMOTE_CONSOLE(RemoteConsoleCommandSender.class),
    CONSOLE(ConsoleCommandSender.class),
    PLAYER(Player.class),
    OTHER(Void.class);

    private final Class m_clazz;

    /**
     * @param clazz the class representing
     */
    SenderType(Class clazz){
        m_clazz = clazz;
    }

    /**
     * Returns the enum value for the sender
     * @param sender the sender to parse
     * @return the SenderType or null if not known
     */
    public static SenderType getFromCommandSender(CommandSender sender){
        for(SenderType type : SenderType.values()){
            if(type.getClassType().isAssignableFrom(sender.getClass())){
                return type;
            }
        }
        return OTHER;
    }

    /**
     * @return the class for this enum
     */
    private Class getClassType() {
        return m_clazz;
    }
}
