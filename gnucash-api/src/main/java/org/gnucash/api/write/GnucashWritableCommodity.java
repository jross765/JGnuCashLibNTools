package org.gnucash.api.write;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.write.impl.ObjectCascadeException;

/**
 * Commodity that can be modified.
 * 
 * @see GnucashCommodity
 */
public interface GnucashWritableCommodity extends GnucashCommodity
{

    void remove() throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException;
    
    // ---------------------------------------------------------------

    void setQualifID(GCshCmdtyCurrID qualifId) throws InvalidCmdtyCurrTypeException;

    void setXCode(String xCode);

    void setName(String name);

    void setFraction(Integer fract);
}
