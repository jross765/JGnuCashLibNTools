package org.gnucash.api.write;

import java.util.List;

import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
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

	/**
	 * Removes the commodity.
	 * 
	 * @throws ObjectCascadeException
	 */
    void remove() throws ObjectCascadeException;
    
    // ------------------------------------------------------------
    
    List<GnuCashWritableAccount> getWritableStockAccounts();

	// ---------------------------------------------------------------

    /**
     * 
     * @param symb
     * 
     * @see #getSymbol()
     */
	void setSymbol(String symb);

	/**
	 * 
	 * @param xCode
	 * 
	 * @see #getXCode()
	 */
    void setXCode(String xCode);

    // ---------------------------------------------------------------

    /**
     * 
     * @param qualifID
     * 
     * @see #getQualifID()
     */
    void setQualifID(GCshCmdtyCurrID qualifID);

    /**
     * 
     * @param name
     * 
     * @see #getName()
     */
    void setName(String name);

    /**
     * 
     * @param fract
     * 
     * @see #getFraction()
     */
    void setFraction(Integer fract);
}
