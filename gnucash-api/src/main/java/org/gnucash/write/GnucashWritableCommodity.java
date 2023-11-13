package org.gnucash.write;

import org.gnucash.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.read.GnucashCommodity;
import org.gnucash.write.impl.ObjectCascadeException;

/**
 * Commodity that can be modified
 */
public interface GnucashWritableCommodity extends GnucashCommodity
{

    void remove() throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException;
    
    // ---------------------------------------------------------------

    public void setQualifId(GCshCmdtyCurrID qualifId) throws InvalidCmdtyCurrTypeException;

    public void setXCode(String xCode);

    public void setName(String name);

    public void setFraction(Integer fract);
}
