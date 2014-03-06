package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import uk.co.eluinhost.commands.Command;
import uk.co.eluinhost.commands.CommandRequest;
import uk.co.eluinhost.ultrahardcore.borders.BorderCreator;
import uk.co.eluinhost.ultrahardcore.borders.BorderTypeManager;
import uk.co.eluinhost.ultrahardcore.borders.SessionManager;
import uk.co.eluinhost.ultrahardcore.borders.types.Border;
import uk.co.eluinhost.ultrahardcore.borders.exceptions.TooManyBlocksException;
import uk.co.eluinhost.configuration.ConfigManager;

import java.util.Collection;

public class BorderCommand extends SimpleCommand {

    public static final String GENERATE_BORDER = "UHC.generateborder";

    /**
     * Ran on /genborder {worldname} {radius} {typeID}[:blockID][:meta] [x,z]
     * @param request request params
     */
    @Command(trigger = "genborder",
            identifier = "BorderCommand",
            permission = GENERATE_BORDER,
            minArgs = 3,
            maxArgs = 4)
    public void onBorderCommand(CommandRequest request){
        World world = request.getWorld(0);
        if(world == null){
            request.sendMessage(translate("INVALID_WORLD").replaceAll("%world%",request.getFirstArg()));
            return;
        }
        if(!request.isArgInt(1)){
            request.sendMessage(translate("INVALID_RADIUS").replaceAll("%radius%",request.getArg(1)));
            return;
        }
        int radius = request.getInt(1);

        BorderTypeManager manager = BorderTypeManager.getInstance();
        FileConfiguration config = ConfigManager.getInstance().getConfig();

        String borderName = request.getArg(2);
        int blockID = config.getInt("border.id");
        int metaID = config.getInt("border.meta");
        if(borderName.contains(":")){
            String[] parts = borderName.split(":");
            borderName = parts[0];
            try{
                blockID = Integer.parseInt(parts[1]);
            }catch (NumberFormatException ignored){
                request.sendMessage(translate("INVALID_BLOCK_ID").replaceAll("%blockID%",parts[1]));
                return;
            }
            if(parts.length > 2){
                try{
                    metaID = Integer.parseInt(parts[2]);
                }catch (NumberFormatException ignored){
                    request.sendMessage(translate("INVALID_META_ID").replaceAll("%meta%",parts[2]));
                    return;
                }
            }
        }
        Border borderType = manager.getBorderByID(borderName);
        if(borderType == null){
            request.sendMessage(translate("INVALID_BORDER_ID").replaceAll("%borderID%",borderName));
            return;
        }

        Location center = world.getSpawnLocation();

        if(request.isArgPresent(3)){
            String coords = request.getArg(3);
            String[] parts = coords.split(",");
            if(parts.length != 2){
                request.sendMessage(translate("INVALID_COORDINATES").replaceAll("%coords%",request.getArg(3)));
                return;
            }
            try{
                double x = Double.parseDouble(parts[0]);
                double z = Double.parseDouble(parts[1]);
                center.setX(x);
                center.setZ(z);
            }catch(NumberFormatException ignored){
                request.sendMessage(translate("INVALID_COORDINATES").replaceAll("%coords%",request.getArg(3)));
                return;
            }
        }

        BorderCreator creator = new BorderCreator(borderType);
        creator.setBlockID(blockID);
        creator.setBlockMeta(metaID);
        creator.setCenter(center);
        creator.setRadius(radius);

        try {
            creator.createBorder();
        } catch (TooManyBlocksException ignored) {
            request.sendMessage(translate("MAX_CHANGED_BLOCKS"));
            return;
        }

        request.sendMessage(translate("BORDER_CREATED"));
    }

    /**
     * Ran on /genborder undo
     * @param request request params
     */
    @Command(trigger = "undo",
            identifier = "BorderUndoCommand",
            minArgs = 0,
            maxArgs = 1,
            permission = GENERATE_BORDER,
            parentID = "BorderCommand")
    public void onBorderUndoCommand(CommandRequest request){
        CommandSender sender = request.getSender();
        String world;
        if (request.getArgs().size() == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(translate("WORLD_NON_PLAYER"));
                return;
            }
            world = ((Entity) sender).getWorld().getName();
        } else {
            world = request.getFirstArg();
        }
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.undoLastSession(world)) {
            sender.sendMessage(translate("UNDONE"));
        } else {
            sender.sendMessage(translate("NOTHING_TO_UNDO"));
        }
    }

    /**
     * Ran on /genborder types
     * @param request request params
     */
    @Command(trigger = "types",
            identifier = "BorderTypesCommand",
            minArgs = 0,
            maxArgs = 0,
            permission = GENERATE_BORDER,
            parentID = "BorderCommand")
    public void onBorderTypesCommand(CommandRequest request){
        Collection<Border> types = BorderTypeManager.getInstance().getTypes();
        CommandSender sender = request.getSender();
        if(types.isEmpty()){
            request.getSender().sendMessage(translate("NO_BORDERS_LOADED"));
            return;
        }
        sender.sendMessage(translate("LOADED_BORDER_TYPES").replaceAll("%count%",String.valueOf(types.size())));
        for(Border border : types){
            sender.sendMessage(translate("BORDER_TYPE_FORMAT").replaceAll("%featureID%",border.getID()).replaceAll("%desc%",border.getDescription()));
        }
    }
}
