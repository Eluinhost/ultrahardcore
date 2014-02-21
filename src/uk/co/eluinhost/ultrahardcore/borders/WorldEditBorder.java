package uk.co.eluinhost.ultrahardcore.borders;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;

public abstract class WorldEditBorder {

    public abstract String getID();

    public abstract String getDescription();

    protected abstract void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException;

    @Override
    public boolean equals(Object o) {
        return o instanceof WorldEditBorder && ((WorldEditBorder) o).getID().equals(getID());
    }
}
