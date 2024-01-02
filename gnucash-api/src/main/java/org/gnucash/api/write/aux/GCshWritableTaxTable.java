package org.gnucash.api.write.aux;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;

public interface GCshWritableTaxTable extends GCshTaxTable {
    
    void setName(final String name);
    
    void setParentID(final GCshID prntID);

    void setParent(final GCshTaxTable prnt);
    
    // ---------------------------------------------------------------
    
//    void addEntry(final GCshTaxTableEntry entry);
//    
//    void removeEntry(GCshTaxTableEntry entry);

}
