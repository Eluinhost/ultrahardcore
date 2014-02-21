package uk.co.eluinhost.ultrahardcore.borders;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.World;

import uk.co.eluinhost.ultrahardcore.borders.types.Cylinder;
import uk.co.eluinhost.ultrahardcore.borders.types.Roofing;
import uk.co.eluinhost.ultrahardcore.borders.types.Square;
import uk.co.eluinhost.ultrahardcore.exceptions.borders.BorderTypeNotFoundException;
import uk.co.eluinhost.ultrahardcore.exceptions.worldedit.WorldEditMaxChangedBlocksException;
import uk.co.eluinhost.ultrahardcore.exceptions.generic.WorldNotFoundException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;

/**
 * World edit references from BorderCreator to go here and stop classdefnotfound
 *
 * @author ghowden
 */
@SuppressWarnings("UtilityClass")
public abstract class WorldEditBorderCreator {

    private static final HashMap<String, LinkedList<EditSession>> SESSIONS = new HashMap<String, LinkedList<EditSession>>();

    private static final List<WorldEditBorder> BORDER_TYPES = new ArrayList<WorldEditBorder>();

    public static List<WorldEditBorder> getTypes() {
        return Collections.unmodifiableList(BORDER_TYPES);
    }

    public static void build(BorderParams bp) throws WorldEditMaxChangedBlocksException, WorldNotFoundException, BorderTypeNotFoundException {
        try {
            World w = Bukkit.getWorld(bp.getWorldName());
            if (w == null) {
                throw new WorldNotFoundException();
            }
            if (!SESSIONS.containsKey(w.getName())) {
                SESSIONS.put(w.getName(), new LinkedList<EditSession>());
            }
            WorldEditBorder web = getBorderByID(bp.getTypeID());
            if (web == null) {
                throw new BorderTypeNotFoundException();
            }
            AbstractList<EditSession> esl = SESSIONS.get(w.getName());
            EditSession es = new EditSession(new BukkitWorld(w), Integer.MAX_VALUE);
            esl.add(es);
            web.createBorder(bp, es);
        } catch (MaxChangedBlocksException ignored) {
            throw new WorldEditMaxChangedBlocksException();
        }
    }

    public static List<String> getBorderIDs() {
        ArrayList<String> r = new ArrayList<String>();
        for (WorldEditBorder web : BORDER_TYPES) {
            r.add(web.getID());
        }
        return r;
    }

    public static WorldEditBorder getBorderByID(String id) {
        for (WorldEditBorder web : BORDER_TYPES) {
            if (web.getID().equals(id)) {
                return web;
            }
        }
        return null;
    }

    public static boolean undoForWorld(String world) {
        LinkedList<EditSession> es = SESSIONS.get(world);
        if (es == null || es.isEmpty()) {
            return false;
        }
        es.getLast().undo(es.getLast());
        es.removeLast();
        return true;
    }

    public static void initialize() {
        BORDER_TYPES.clear();
        BORDER_TYPES.add(new Cylinder());
        BORDER_TYPES.add(new Square());
        BORDER_TYPES.add(new Roofing());
    }
}
