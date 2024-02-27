package org.gnucash.api.write.aux;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;

public interface GCshWritableTaxTable extends GCshTaxTable {
    
    void setName(String name);
    
    void setParentID(GCshID prntID);

    void setParent(GCshTaxTable prnt);
    
    // ---------------------------------------------------------------
    
    void addEntry(GCshTaxTableEntry entry);
    
    void removeEntry(GCshTaxTableEntry entry);

}
