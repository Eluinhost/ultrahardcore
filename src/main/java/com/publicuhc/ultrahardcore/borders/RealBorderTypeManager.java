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
