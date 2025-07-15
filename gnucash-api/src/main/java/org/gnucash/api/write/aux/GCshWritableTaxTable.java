package org.gnucash.api.write.aux;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;

public interface GCshWritableTaxTable extends GCshTaxTable {
    
	/**
	 * 
	 * @param name
	 * 
	 * @see #getName()
	 */
    void setName(String name);
    
    /**
     * 
     * @param prntID
     * 
     * @see #getParentID()
     * @see #setParentID(GCshID)
     */
    void setParentID(GCshID prntID);

    /**
     * 
     * @param prnt
     * 
     * @see #getParent()
     */
    void setParent(GCshTaxTable prnt);
    
    // ---------------------------------------------------------------
    
    /**
     * 
     * @param entry
     * 
     * @see #getEntries()
     * @see #removeEntry(GCshTaxTableEntry)
     */
    void addEntry(GCshTaxTableEntry entry);
    
    /**
     * 
     * @param entry
     *
     * @see #getEntries()
     * @see #addEntry(GCshTaxTableEntry)
     */
    void removeEntry(GCshTaxTableEntry entry);

}
