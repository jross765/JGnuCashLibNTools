package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.base.basetypes.simple.GCshID;

public interface GCshWritableBillTerms extends GCshBillTerms {

	/**
	 * 
	 * @param refCnt
	 * 
	 * @see #getRefcount()
	 */
    void setRefcount(int refCnt);

    /**
     * 
     * @param name
     * 
     * @see #getName()
     */
    void setName(String name);

    /**
     * 
     * @param descr
     * 
     * @see #getDescription()
     */
    void setDescription(String descr);

    /**
     * 
     * @param val
     * 
     * @see #isInvisible()
     */
    void setInvisible(boolean val);
    
    // ----------------------------
    
    /**
     * 
     * @return
     * 
     * @see #getDays()
     */
    GCshWritableBillTermsDays getWritableDays();

    /**
     * 
     * @return
     * 
     * @see {@link #getProximo()}
     */
    GCshWritableBillTermsProximo getWritableProximo();

    // ----------------------------
    
    /**
     * 
     * @param type
     * 
     * @see #getType()
     */
    void setType(Type type);

    /**
     * 
     * @param bllTrmsDays
     * 
     * @see #getDays()
     */
    void setDays(GCshWritableBillTermsDays bllTrmsDays);

    /**
     * 
     * @param bllTrmsProx
     * 
     * @see #getProximo()
     */
    void setProximo(GCshWritableBillTermsProximo bllTrmsProx );

    // ----------------------------
    
    /**
     * 
     * @param prntID
     * 
     * @see #getParentID()
     */
    void setParentID(GCshID prntID);

    /**
     * 
     * @param chld
     * 
     * @see #getChildren()
     * @see #removeChild(String)
     */
    void addChild(String chld);

    /**
     * 
     * @param chld
     * 
     * @see #getChildren()
     * @see #addChild(String)
     */
    void removeChild(String chld);

}
