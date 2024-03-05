package org.gnucash.api.write;

import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.api.write.impl.ObjectCascadeException;

/**
 * Commodity that can be modified.
 * 
 * @see GnuCashCommodity
 */
public interface GnuCashWritableCommodity extends GnuCashCommodity,
                                                  GnuCashWritableObject,
                                                  HasWritableUserDefinedAttributes
{

    void remove() throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException;
    
    // ---------------------------------------------------------------

    void setQualifID(GCshCmdtyCurrID qualifId) throws InvalidCmdtyCurrTypeException;

    void setXCode(String xCode);

    void setName(String name);

    void setFraction(Integer fract);
}
