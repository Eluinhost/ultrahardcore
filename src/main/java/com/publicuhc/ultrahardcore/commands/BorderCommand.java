package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.shaded.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import com.publicuhc.commands.Command;
import com.publicuhc.commands.CommandRequest;
import com.publicuhc.configuration.ConfigManager;
import com.publicuhc.ultrahardcore.borders.BorderCreator;
import com.publicuhc.ultrahardcore.borders.BorderTypeManager;
import com.publicuhc.ultrahardcore.borders.SessionManager;
import com.publicuhc.ultrahardcore.borders.exceptions.TooManyBlocksException;
import com.publicuhc.ultrahardcore.borders.types.Border;

import java.util.Collection;

public class BorderCommand extends SimpleCommand {

    public static final String GENERATE_BORDER = "UHC.generateborder";

    private final BorderTypeManager m_borderTypes;

    /**
     * Border commands
     * @param border the border type manager
     * @param config the config manager
     */
    @Inject
    private BorderCommand(BorderTypeManager border, ConfigManager config){
        super(config);
        m_borderTypes = border;
    }

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
            request.sendMessage(translate("border.invalid.world").replaceAll("%world%",request.getFirstArg()));
            return;
        }
        if(!request.isArgNumber(1)){
            request.sendMessage(translate("border.invalid.radius").replaceAll("%radius%",request.getArg(1)));
            return;
        }
        double radius = request.getNumber(1).doubleValue();

        FileConfiguration config = getConfigManager().getConfig();

        String borderName = request.getArg(2);
        Material blockID = Material.matchMaterial(config.getString("border.id"));
        int metaID = config.getInt("border.meta");
        if(borderName.contains(":")){
            String[] parts = borderName.split(":");
            borderName = parts[0];
            blockID = Material.matchMaterial(parts[1]);
            if(parts.length > 2){
                try{
                    metaID = Integer.parseInt(parts[2]);
                }catch (NumberFormatException ignored){
                    request.sendMessage(translate("border.invalid.meta_id").replaceAll("%meta%",parts[2]));
                    return;
                }
            }
        }
        if(blockID == null) {
            request.sendMessage(translate("border.invalid.block_id"));
            return;
        }
        if(!blockID.isBlock()){
            request.sendMessage(translate("border.invalid.block_id_not_block"));
            return;
        }

        Border borderType = m_borderTypes.getBorderByID(borderName);
        if(borderType == null){
            request.sendMessage(translate("border.invalid.border_id").replaceAll("%borderID%",borderName));
            return;
        }

        Location center = world.getSpawnLocation();

        if(request.isArgPresent(3)){
            String coords = request.getArg(3);
            String[] parts = coords.split(",");
            if(parts.length != 2){
                request.sendMessage(translate("border.invalid.coordinates").replaceAll("%coords%",request.getArg(3)));
                return;
            }
            try{
                double x = Double.parseDouble(parts[0]);
                double z = Double.parseDouble(parts[1]);
                center.setX(x);
                center.setZ(z);
            }catch(NumberFormatException ignored){
                request.sendMessage(translate("border.invalid.coordinates").replaceAll("%coords%",request.getArg(3)));
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
            request.sendMessage(translate("border.max_blocks"));
            return;
        }

        request.sendMessage(translate("border.created"));
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
                request.sendMessage(translate("border.non_player_world"));
                return;
            }
            world = ((Entity) sender).getWorld().getName();
        } else {
            world = request.getFirstArg();
        }
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.undoLastSession(world)) {
            request.sendMessage(translate("border.undone"));
        } else {
            request.sendMessage(translate("border.no_undo"));
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
        Collection<Border> types = m_borderTypes.getTypes();
        if(types.isEmpty()){
            request.sendMessage(translate("border.no_borders"));
            return;
        }
        request.sendMessage(translate("border.border_types").replaceAll("%count%",String.valueOf(types.size())));
        for(Border border : types){
            request.sendMessage(translate("border.type_format").replaceAll("%featureID%",border.getID()).replaceAll("%desc%",border.getDescription()));
        }
    }
}
