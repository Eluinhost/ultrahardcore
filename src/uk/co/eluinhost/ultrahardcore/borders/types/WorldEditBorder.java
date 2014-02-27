package uk.co.eluinhost.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import org.apache.commons.lang.builder.HashCodeBuilder;
import uk.co.eluinhost.ultrahardcore.borders.BorderParams;

public abstract class WorldEditBorder {

    private final String m_borderID;
    private final String m_description;

    /**
     * @return the ID of this border
     */
    public String getID(){
        return m_borderID;
    }

    /**
     * @return the description of this border
     */
    public String getDescription(){
        return m_description;
    }

    /**
     * Make a new border
     * @param id the id of the border
     * @param description the borders description
     */
    protected WorldEditBorder(String id, String description){
        m_borderID = id;
        m_description = description;
    }

    /**
     * Create the border using the editsession and parameters
     * @param bp the border parameters
     * @param es the editsession to use
     * @throws MaxChangedBlocksException when worldedit complains about too many blocks
     */
    public abstract void createBorder(BorderParams bp, EditSession es) throws MaxChangedBlocksException;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorldEditBorder && ((WorldEditBorder) obj).getID().equals(getID());
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getID()).toHashCode();
    }
}
