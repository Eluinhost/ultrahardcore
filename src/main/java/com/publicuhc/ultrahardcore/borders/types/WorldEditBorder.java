/*
 * WorldEditBorder.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.borders.types;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Location;
import com.publicuhc.ultrahardcore.borders.SessionManager;
import com.publicuhc.ultrahardcore.borders.exceptions.TooManyBlocksException;
import org.bukkit.Material;

public abstract class WorldEditBorder implements Border {

    private final String m_borderID;
    private final String m_description;


    @Override
    public String getID(){
        return m_borderID;
    }

    /**
     * @return the description of this border
     */
    @Override
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
     * @param center the center location
     * @param radius the radius to use
     * @param blockID the block ID to use
     * @param blockMeta the block data value
     * @param es the editsession to use
     * @throws com.sk89q.worldedit.MaxChangedBlocksException when worldedit complains
     */
    protected abstract void createBorder(Location center, double radius, Material blockID, int blockMeta, EditSession es) throws MaxChangedBlocksException;

    @Override
    public final void build(Location center, double radius, Material blockID, int blockMeta) throws TooManyBlocksException {
        try {
            createBorder(center,radius,blockID,blockMeta, SessionManager.getInstance().getNewEditSession(center.getWorld()));
        } catch (MaxChangedBlocksException ignored) {
            throw new TooManyBlocksException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Border && ((Border) obj).getID().equals(getID());
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder(17, 31).append(getID()).toHashCode();
    }
}
