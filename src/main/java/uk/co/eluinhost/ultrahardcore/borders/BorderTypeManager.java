package uk.co.eluinhost.ultrahardcore.borders;

import com.google.inject.Singleton;
import uk.co.eluinhost.ultrahardcore.borders.exceptions.BorderIDConflictException;
import uk.co.eluinhost.ultrahardcore.borders.types.Border;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Singleton
public class BorderTypeManager {

    private final Collection<Border> m_borders = new ArrayList<Border>();

    /**
     * @param id border ID
     * @return border if exists, null if not
     */
    public Border getBorderByID(String id){
        for(Border border : m_borders){
            if(border.getID().equalsIgnoreCase(id)){
                return border;
            }
        }
        return null;
    }

    /**
     * Add the border to the list
     * @param border the border
     * @throws BorderIDConflictException if ID is already taken
     */
    public void addBorder(Border border) throws BorderIDConflictException {
        if(getBorderByID(border.getID()) != null){
            throw new BorderIDConflictException();
        }
        m_borders.add(border);
    }

    /**
     * @return unmodifiable collection of border types
     */
    public Collection<Border> getTypes(){
        return Collections.unmodifiableCollection(m_borders);
    }
}
