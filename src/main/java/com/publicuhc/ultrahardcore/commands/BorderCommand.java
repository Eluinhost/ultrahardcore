package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.matchers.PatternRouteMatcher;
import com.publicuhc.pluginframework.commands.matchers.SimpleRouteMatcher;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routing.DefaultMethodRoute;
import com.publicuhc.pluginframework.commands.routing.MethodRoute;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.borders.BorderCreator;
import com.publicuhc.ultrahardcore.borders.BorderTypeManager;
import com.publicuhc.ultrahardcore.borders.SessionManager;
import com.publicuhc.ultrahardcore.borders.exceptions.TooManyBlocksException;
import com.publicuhc.ultrahardcore.borders.types.Border;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BorderCommand extends SimpleCommand {

    public static final String GENERATE_BORDER = "UHC.generateborder";

    private final BorderTypeManager m_borderTypes;

    /**
     * Border commands
     * @param border the border type manager
     * @param config the config manager
     * @param translate the translator
     */
    @Inject
    private BorderCommand(BorderTypeManager border, Configurator config, Translate translate){
        super(config, translate);
        m_borderTypes = border;
    }

    /**
     * Ran on /genborder {worldname} {radius} {typeID}[:blockID][:meta] [x,z]
     * @param request request params
     */
    @CommandMethod
    public void borderCommand(CommandRequest request){
        World world = request.getWorld(0);
        if(world == null){
            request.sendMessage(translate("border.invalid.world", locale(request.getSender()), "world", request.getFirstArg()));
            return;
        }
        if(!request.isArgNumber(1)){
            request.sendMessage(translate("border.invalid.radius", locale(request.getSender()), "radius", request.getArg(1)));
            return;
        }
        double radius = request.getNumber(1).doubleValue();

        FileConfiguration config = getConfigurator().getConfig("main");

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
                    request.sendMessage(translate("border.invalid.meta_id", locale(request.getSender()), "meta", parts[2]));
                    return;
                }
            }
        }
        if(blockID == null) {
            request.sendMessage(translate("border.invalid.block_id", locale(request.getSender())));
            return;
        }
        if(!blockID.isBlock()){
            request.sendMessage(translate("border.invalid.block_id_not_block", locale(request.getSender())));
            return;
        }

        Border borderType = m_borderTypes.getBorderByID(borderName);
        if(borderType == null){
            request.sendMessage(translate("border.invalid.border_id", locale(request.getSender()), "borderID", borderName));
            return;
        }

        Location center = world.getSpawnLocation();

        if(request.isArgPresent(3)){
            String coords = request.getArg(3);
            String[] parts = coords.split(",");
            if(parts.length != 2){
                request.sendMessage(translate("border.invalid.coordinates", locale(request.getSender()), "coords", request.getArg(3)));
                return;
            }
            try{
                double x = Double.parseDouble(parts[0]);
                double z = Double.parseDouble(parts[1]);
                center.setX(x);
                center.setZ(z);
            }catch(NumberFormatException ignored){
                request.sendMessage(translate("border.invalid.coordinates", locale(request.getSender()), "coords", request.getArg(3)));
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
            request.sendMessage(translate("border.max_blocks", locale(request.getSender())));
            return;
        }

        request.sendMessage(translate("border.created", locale(request.getSender())));
    }

    /**
     * Run on 3 or more parameters
     * @return the route
     */
    @RouteInfo
    public MethodRoute borderCommandDetails() {
        return new DefaultMethodRoute(
                new PatternRouteMatcher(Pattern.compile("([\\S]+ ){2}[\\S]+.*")),
                new SenderType[] {
                        SenderType.PLAYER,
                        SenderType.CONSOLE,
                        SenderType.COMMAND_BLOCK,
                        SenderType.REMOTE_CONSOLE
                },
                GENERATE_BORDER,
                "genborder"
        );
    }

    /**
     * Ran on /genborder undo
     * @param request request params
     */
    @CommandMethod
    public void borderUndoCommand(CommandRequest request){
        CommandSender sender = request.getSender();
        String world;
        if (request.getArgs().size() == 1) {
            if (!(sender instanceof Player)) {
                request.sendMessage(translate("border.non_player_world", locale(request.getSender())));
                return;
            }
            world = ((Entity) sender).getWorld().getName();
        } else {
            world = request.getFirstArg();
        }
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager.undoLastSession(world)) {
            request.sendMessage(translate("border.undone", locale(request.getSender())));
        } else {
            request.sendMessage(translate("border.no_undo", locale(request.getSender())));
        }
    }

    /**
     * Run on /genborder undo
     * @return the route
     */
    @RouteInfo
    public MethodRoute borderUndoCommandDetails() {
        return new DefaultMethodRoute(
                new SimpleRouteMatcher("undo"),
                new SenderType[] {
                        SenderType.PLAYER,
                        SenderType.CONSOLE,
                        SenderType.COMMAND_BLOCK,
                        SenderType.REMOTE_CONSOLE
                },
                GENERATE_BORDER,
                "genborder"
        );
    }

    /**
     * Ran on /genborder types
     * @param request request params
     */
    @CommandMethod
    public void onBorderTypesCommand(CommandRequest request){
        Collection<Border> types = m_borderTypes.getTypes();
        if(types.isEmpty()){
            request.sendMessage(translate("border.no_borders", locale(request.getSender())));
            return;
        }
        request.sendMessage(translate("border.border_types", locale(request.getSender()), "count", String.valueOf(types.size())));
        for(Border border : types){
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("featureID", border.getID());
            vars.put("desc", border.getDescription());
            request.sendMessage(translate("border.type_format", locale(request.getSender()), vars));
        }
    }

    /**
     * Run on /genborder types
     * @return the route
     */
    @RouteInfo
    public MethodRoute borderTypesCommand() {
        return new DefaultMethodRoute(
                new SimpleRouteMatcher("types"),
                new SenderType[] {
                        SenderType.PLAYER,
                        SenderType.CONSOLE,
                        SenderType.COMMAND_BLOCK,
                        SenderType.REMOTE_CONSOLE
                },
                GENERATE_BORDER,
                "genborder"
        );
    }
}
