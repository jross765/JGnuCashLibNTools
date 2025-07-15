package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.simple.GCshID;

/**
 * Generic job that can be modified.
 * 
 * @see GnuCashGenerJob
 */
public interface GnuCashWritableGenerJob extends GnuCashGenerJob,
                                                 GnuCashWritableObject
{
    
	/**
	 * 
	 * @param ownID
	 * 
	 * @see #getOwnerID()
	 */
    void setOwnerID(GCshID ownID);

    /**
     * 
     * @param own
     * 
     * @see #getOwner()
     */
    void setOwner(GCshOwner own);

    // -----------------------------------------------------------

    /**
     * 
     * @param number
     * 
     * @see #getNumber()
     */
    void setNumber(String number);

    /**
     * Set the description-text.
     *
     * @param desc the new description
     * 
     * @see #getName()
     */
    void setName(String desc);

    /**
     * @param jobActive true is the job is to be (re)activated, false to deactivate
     * 
     * @see #getActive()
     */
    public void setActive(boolean jobActive);

}
