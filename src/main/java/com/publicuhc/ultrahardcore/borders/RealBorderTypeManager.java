/*
 * RealBorderTypeManager.java
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

package com.publicuhc.ultrahardcore.borders;

import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.ultrahardcore.borders.exceptions.BorderIDConflictException;
import com.publicuhc.ultrahardcore.borders.types.Border;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Singleton
public class RealBorderTypeManager implements BorderTypeManager {

    private final Collection<Border> m_borders = new ArrayList<Border>();

    @Override
    public Border getBorderByID(String id){
        for(Border border : m_borders){
            if(border.getID().equalsIgnoreCase(id)){
                return border;
            }
        }
        return null;
    }

    @Override
    public void addBorder(Border border) throws BorderIDConflictException {
        if(getBorderByID(border.getID()) != null){
            throw new BorderIDConflictException();
        }
        m_borders.add(border);
    }

    @Override
    public Collection<Border> getTypes(){
        return Collections.unmodifiableCollection(m_borders);
    }
}
