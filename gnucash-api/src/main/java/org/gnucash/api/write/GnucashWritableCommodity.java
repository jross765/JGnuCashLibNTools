package org.gnucash.api.write;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.write.impl.ObjectCascadeException;

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
