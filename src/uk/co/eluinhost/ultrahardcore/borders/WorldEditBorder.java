package uk.co.eluinhost.ultrahardcore.borders;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class WorldEditBorder {

    public abstract String getID();

    public abstract String getDescription();

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
