package org.gnucash.read.aux;

import java.util.List;

import org.gnucash.basetypes.simple.GCshID;

public interface GCshBillTerms {

    public enum Type {
	DAYS,
	PROXIMO
    }

    // -----------------------------------------------------------

    public GCshID getId();

    public int getRefcount();

    public String getName();

    public String getDescription();

    public boolean isInvisible();
    
    // ----------------------------
    
    public Type getType() throws BillTermsTypeException;

    public GCshBillTermsDays getDays();

    public GCshBillTermsProximo getProximo();

    // ----------------------------
    
    public String getParentId();

    public List<String> getChildren();

}
