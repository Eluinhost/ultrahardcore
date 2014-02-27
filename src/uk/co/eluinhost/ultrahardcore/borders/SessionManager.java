package uk.co.eluinhost.ultrahardcore.borders;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

//TODO minor per player per world storage
public class SessionManager {

   //Map of world name to stack of sessions run on the world
    private final Map<String, Stack<EditSession>> m_sessions = new HashMap<String, Stack<EditSession>>();

    private static final int MAX_ALLOWED_BLOCKS = Integer.MAX_VALUE;

    private static final class SessionManagerHolder {
        private static final SessionManager SESSION_MANAGER = new SessionManager();
    }

    /**
     * Get the instance of the session manager
     * @return the session manager
     */
    public static SessionManager getInstance(){
        return SessionManagerHolder.SESSION_MANAGER;
    }

    /**
     * Handles world edit sessions
     */
    private SessionManager(){}

    /**
     * Undoes the last edit session for the world
     * @param worldName the world to undo for
     * @return true if something happened, false if no world or sessions to undo
     */
    public boolean undoLastSession(String worldName){
        Stack<EditSession> sessions = m_sessions.get(worldName);
        if(null == sessions || sessions.isEmpty()){
            return false;
        }
        EditSession session = sessions.pop();
        session.undo(session);
        return true;
    }

    /**
     * Get a new edit session
     * @param world the world to make the session for
     * @return the edit session
     */
    public EditSession getNewEditSession(World world){
        String worldName = world.getName();
        Stack<EditSession> sessions = m_sessions.get(worldName);
        if(null == sessions){
            sessions = new Stack<EditSession>();
            m_sessions.put(worldName,sessions);
        }
        EditSession session = new EditSession(new BukkitWorld(world), MAX_ALLOWED_BLOCKS);
        sessions.push(session);
        return session;
    }
}
