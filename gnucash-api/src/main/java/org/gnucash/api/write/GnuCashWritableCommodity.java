package org.gnucash.api.write;

import java.util.List;

import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.api.write.impl.ObjectCascadeException;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;

/**
 * Commodity that can be modified.
 * 
 * @see GnuCashCommodity
 */
public interface GnuCashWritableCommodity extends GnuCashCommodity,
                                                  GnuCashWritableObject,
                                                  HasWritableUserDefinedAttributes
{

    void remove() throws ObjectCascadeException;
    
    // ------------------------------------------------------------
    
    List<GnuCashWritableAccount> getStockWritableAccounts();

    // ---------------------------------------------------------------

    void setQualifID(GCshCmdtyCurrID qualifId);

    void setXCode(String xCode);

    void setName(String name);

    void setFraction(Integer fract);
}
