package com.publicuhc.ultrahardcore.borders;

import com.publicuhc.ultrahardcore.borders.exceptions.BorderIDConflictException;
import com.publicuhc.ultrahardcore.borders.types.Border;

import java.util.Collection;

public interface BorderTypeManager {

   /**
    * @param id border ID
    * @return border if exists, null if not
    */
    Border getBorderByID(String id);

    /**
     * Add the border to the list
     * @param border the border
     * @throws com.publicuhc.ultrahardcore.borders.exceptions.BorderIDConflictException if ID is already taken
     */
    void addBorder(Border border) throws BorderIDConflictException;

    /**
     * @return unmodifiable collection of border types
     */
    Collection<Border> getTypes();
}
