package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.ultrahardcore.borders.BorderCreator;
import uk.co.eluinhost.ultrahardcore.borders.SessionManager;
import uk.co.eluinhost.ultrahardcore.borders.types.CylinderBorder;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.worldedit.TooManyBlocksException;
import uk.co.eluinhost.ultrahardcore.config.ConfigManager;

public class BorderCommand {

    @Command(trigger = "genborder",
            identifier = "BorderCommand",
            permission = PermissionNodes.GENERATE_BORDER)
    public void onBorderCommand(CommandRequest request){
        //TODO this
    }

    @Command(trigger = "undo",
            identifier = "BorderUndoCommand",
            minArgs = 0,
            maxArgs = 1,
            permission = PermissionNodes.GENERATE_BORDER,
            parentID = "BorderCommand")
    public void onBorderUndoCommand(CommandRequest request){
        CommandSender sender = request.getSender();
        String world;
        if (request.getArgs().size() == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You need to specify a world to undo when not ran as a player");
                return;
            }
            world = ((Entity) sender).getWorld().getName();
        } else {
            world = request.getFirstArg();
        }
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.undoLastSession(world)) {
            sender.sendMessage(ChatColor.GOLD + "Undone successfully!");
        } else {
            sender.sendMessage(ChatColor.GOLD + "Nothing left to undo!");
        }
    }

    @Command(trigger = "types",
            identifier = "BorderTypesCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = PermissionNodes.GENERATE_BORDER,
            parentID = "BorderCommand")
    public void onBorderTypesCommand(CommandRequest request){
        //TODO this
    }

    private static final String SYNTAX = "/generateborder radius world[:x,z] typeID[:blockid:meta] OR /generateborder undo/types [world]";

    public boolean onCommand(CommandSender sender, Command command, String label,
                             String[] args) {
        if ("generateborder".equals("")) {
            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax: " + SYNTAX);
                return true;
            }
            int radius;
            try {
                radius = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + "Unknown radius size: " + args[0]);
                return true;
            }
            int x;
            int z;
            World w;
            if (args[1].contains(":")) {
                String[] parts = args[1].split(":");
                if (parts.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid world name/coordinates, syntax for world is worldname:x,z");
                    return true;
                }
                String[] parts2 = parts[1].split(",");
                if (parts2.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid world name/coordinates, syntax for world is worldname:x,z");
                    return true;
                }
                try {
                    x = Integer.parseInt(parts2[0]);
                    z = Integer.parseInt(parts2[1]);
                    args[1] = parts[0];
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "One or more world coordinates not a number, world syntax is worldname:x,z");
                    return true;
                }
                w = Bukkit.getWorld(args[1]);
                if (w == null) {
                    sender.sendMessage(ChatColor.RED + "World " + args[1] + " not found!");
                    return true;
                }
            } else {
                w = Bukkit.getWorld(args[1]);
                if (w == null) {
                    sender.sendMessage(ChatColor.RED + "World " + args[1] + " not found!");
                    return true;
                }
                x = w.getSpawnLocation().getBlockX();
                z = w.getSpawnLocation().getBlockZ();
            }
            String[] blockinfo;
            if (args[2].contains(":")) {
                String[] blockinfos = args[2].split(":");
                if (blockinfos.length != 3) {
                    sender.sendMessage(ChatColor.RED + "Unknown block ID and meta, syntax: " + SYNTAX);
                    return true;
                }
                blockinfo = blockinfos;
            } else {
                blockinfo = new String[]{
                        args[2],
                        ConfigManager.getInstance().getConfig().getString(ConfigNodes.BORDER_BLOCK),
                        ConfigManager.getInstance().getConfig().getString(ConfigNodes.BORDER_BLOCK_META)
                };
            }
            int borderID;
            try {
                borderID = Integer.parseInt(blockinfo[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Unknown number " + blockinfo[1] + " for block ID");
                return true;
            }
            int metaID;
            try {
                metaID = Integer.parseInt(blockinfo[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Unknown number " + blockinfo[2] + " for block meta");
                return true;
            }

            //TODO BLOCKER put right border in here based on blockinfo[0]
            BorderCreator creator = new BorderCreator(new CylinderBorder());
            creator.setBlockID(borderID);
            creator.setBlockMeta(metaID);
            creator.setCenter(null); //TODO generate the location
            creator.setRadius(radius);

            try {
                creator.createBorder();
            } catch (TooManyBlocksException ignored) {
                sender.sendMessage(ChatColor.RED + "Error, hit max changable blocks");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "World border created successfully");
            return true;
        }
        return false;
    }
}
