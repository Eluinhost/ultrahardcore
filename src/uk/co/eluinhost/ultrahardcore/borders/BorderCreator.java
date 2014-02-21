package uk.co.eluinhost.ultrahardcore.borders;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.exceptions.borders.BorderTypeNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.worldedit.WorldEditMaxChangedBlocksException;
import uk.co.eluinhost.ultrahardcore.exceptions.worldedit.WorldEditNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

/**
 * Class to generate a border using worldedit, actual worldedit references are in WorldEditBorderCreator
 *
 * @author ghowden
 */
//TODO stop being a utility type class
//TODO MAJOR cleanup of border code, needs a command based in commands package
public class BorderCreator extends UHCCommand {

    private boolean m_worldEditFound = false;

    //Generate a border that is of the params
    public void generateBorder(BorderParams bp) throws WorldEditNotFoundException, WorldEditMaxChangedBlocksException, WorldNotFoundException, BorderTypeNotFoundException {
        if (m_worldEditFound) {
            WorldEditBorderCreator.build(bp);
        } else {
            throw new WorldEditNotFoundException();
        }
    }

    public BorderCreator() {
        m_worldEditFound = Bukkit.getPluginManager().getPlugin("WorldEdit") != null;
        if (m_worldEditFound) {
            WorldEditBorderCreator.initialize();
        }
    }

    public boolean undoForWorld(String world) throws WorldEditNotFoundException, WorldNotFoundException {
        if (m_worldEditFound) {
            return WorldEditBorderCreator.undoForWorld(world);
        } else {
            throw new WorldEditNotFoundException();
        }
    }

    private static final String SYNTAX = "/generateborder radius world[:x,z] typeID[:blockid:meta] OR /generateborder undo/types [world]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
                             String[] args) {
        if ("generateborder".equals(command.getName())) {
            if (!sender.hasPermission(PermissionNodes.GENERATE_BORDER)) {
                sender.sendMessage(ChatColor.RED + "You don't have permission " + PermissionNodes.GENERATE_BORDER);
                return true;
            }
            if (args.length >= 1) {
                if ("undo".equalsIgnoreCase(args[0])) {
                    try {
                        String world;
                        if (args.length == 1) {
                            if (sender instanceof Player) {
                                world = ((Entity) sender).getWorld().getName();
                            } else {
                                sender.sendMessage("You need to specify a world to undo using the console");
                                return true;
                            }
                        } else {
                            world = args[1];
                        }
                        if (undoForWorld(world)) {
                            sender.sendMessage(ChatColor.GOLD + "Undone successfully!");
                        } else {
                            sender.sendMessage(ChatColor.GOLD + "Nothing left to undo!");
                        }
                        return true;
                    } catch (WorldEditNotFoundException ignored) {
                        sender.sendMessage(ChatColor.RED + "WorldEdit " + args[1] + " not found, required to make borders!");
                        return true;
                    } catch (WorldNotFoundException ignored) {
                        sender.sendMessage(ChatColor.RED + "World " + args[1] + " was not found!");
                        return true;
                    }
                } else if ("types".equals(args[0])) {
                    if (m_worldEditFound) {
                        List<WorldEditBorder> types = WorldEditBorderCreator.getTypes();
                        if (types.isEmpty()) {
                            sender.sendMessage(ChatColor.RED + "No border types loaded!");
                            return true;
                        }
                        sender.sendMessage(ChatColor.GOLD + "Loaded border types: (" + types.size() + ")");
                        for (WorldEditBorder w : WorldEditBorderCreator.getTypes()) {
                            sender.sendMessage(ChatColor.GRAY + w.getID() + " - " + w.getDescription());
                        }
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "World edit was not found! It is needed to use border functions");
                        return true;
                    }
                }
            }
            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax: " + SYNTAX);
                return true;
            }
            int radius;
            try {
                radius = Integer.parseInt(args[0]);
            } catch (Exception ignored) {
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
                } catch (Exception ignored) {
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
                        ConfigHandler.getConfig(ConfigHandler.MAIN).getString(ConfigNodes.BORDER_BLOCK),
                        ConfigHandler.getConfig(ConfigHandler.MAIN).getString(ConfigNodes.BORDER_BLOCK_META)
                };
            }
            int borderID;
            try {
                borderID = Integer.parseInt(blockinfo[1]);
            } catch (Exception ignored) {
                sender.sendMessage(ChatColor.RED + "Unknown number " + blockinfo[1] + " for block ID");
                return true;
            }
            int metaID;
            try {
                metaID = Integer.parseInt(blockinfo[2]);
            } catch (Exception ignored) {
                sender.sendMessage(ChatColor.RED + "Unknown number " + blockinfo[2] + " for block meta");
                return true;
            }

            BorderParams bp = new BorderParams(x, z, radius, blockinfo[0], w.getName(), borderID, metaID);
            try {
                generateBorder(bp);
            } catch (WorldEditNotFoundException ignored) {
                sender.sendMessage(ChatColor.RED + "WorldEdit not found, required to make borders!");
                return true;
            } catch (WorldEditMaxChangedBlocksException ignored) {
                sender.sendMessage(ChatColor.RED + "Error, hit max changable blocks by WorldEdit");
                return true;
            } catch (WorldNotFoundException ignored) {
                sender.sendMessage(ChatColor.RED + "World " + args[1] + " not found!");
                return true;
            } catch (BorderTypeNotFoundException ignored) {
                sender.sendMessage(ChatColor.RED + "The border type " + blockinfo[0] + " was not found!, use /generateborder types to view all types");
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "World border created successfully");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        ArrayList<String> r = new ArrayList<String>();
        if (args.length == 1) {
            r.add("radius");
            r.add("undo");
            r.add("types");
            return r;
        }
        if (args.length == 2) {
            if ("undo".equalsIgnoreCase(args[0])) {
                return ServerUtil.getWorldNames();
            }
            if ("types".equalsIgnoreCase(args[0])) {
                return r;
            }
            return ServerUtil.getWorldNamesWithSpawn();
        }
        if (args.length == 3) {
            return WorldEditBorderCreator.getBorderIDs();
        }
        return r;
    }
}
