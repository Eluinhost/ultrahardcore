package uk.co.eluinhost.ultrahardcore.borders;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class WorldEditBorder {

    private final String m_borderID;
    private final String m_description;

    public String getID(){
        return m_borderID;
    }

    public String getDescription(){
        return m_description;
    }

    protected WorldEditBorder(String id, String description){
        m_borderID = id;
        m_description = description;
    }

    protected abstract void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorldEditBorder && ((WorldEditBorder) obj).getID().equals(getID());
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getID()).toHashCode();
    }
}
