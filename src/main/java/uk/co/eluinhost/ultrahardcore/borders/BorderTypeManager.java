package uk.co.eluinhost.ultrahardcore.borders;

import uk.co.eluinhost.ultrahardcore.borders.exceptions.BorderIDConflictException;
import uk.co.eluinhost.ultrahardcore.borders.types.Border;

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
     * @throws uk.co.eluinhost.ultrahardcore.borders.exceptions.BorderIDConflictException if ID is already taken
     */
    void addBorder(Border border) throws BorderIDConflictException;

    /**
     * @return unmodifiable collection of border types
     */
    Collection<Border> getTypes();
}
