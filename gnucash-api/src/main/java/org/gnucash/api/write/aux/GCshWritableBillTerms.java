package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.base.basetypes.simple.GCshID;

public interface GCshWritableBillTerms extends GCshBillTerms {

    void setRefcount(int refCnt);

    void setName(String name);

    void setDescription(String descr);

    void setInvisible(boolean val);
    
    // ----------------------------
    
    GCshWritableBillTermsDays getWritableDays();

    GCshWritableBillTermsProximo getWritableProximo();

    // ----------------------------
    
    void setType(Type type);

    void setDays(GCshWritableBillTermsDays bllTrmsDays);

    void setProximo(GCshWritableBillTermsProximo bllTrmsProx );

    // ----------------------------
    
    void setParentID(GCshID prntID);

    void addChild(String chld);

    void removeChild(String chld);

}
