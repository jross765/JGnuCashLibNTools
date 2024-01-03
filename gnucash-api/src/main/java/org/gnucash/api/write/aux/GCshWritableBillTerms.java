package org.gnucash.api.write.aux;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.aux.BillTermsTypeException;
import org.gnucash.api.read.aux.GCshBillTerms;

public interface GCshWritableBillTerms extends GCshBillTerms {

    void setRefcount(final int refCnt);

    void setName(final String name);

    void setDescription(final String descr);

    void setInvisible(final boolean val);
    
    // ----------------------------
    
    GCshWritableBillTermsDays getWritableDays();

    GCshWritableBillTermsProximo getWritableProximo();

    // ----------------------------
    
    void setType(final Type type) throws BillTermsTypeException;

    void setDays(final GCshWritableBillTermsDays bllTrmsDays);

    void setProximo(final GCshWritableBillTermsProximo bllTrmsProx );

    // ----------------------------
    
    void setParentID(final GCshID prntID);

    void addChild(final String chld);

    void removeChild(String chld);

}
