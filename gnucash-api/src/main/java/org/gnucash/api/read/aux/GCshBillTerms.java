package org.gnucash.api.read.aux;

import java.util.List;

import org.gnucash.api.basetypes.simple.GCshID;

public interface GCshBillTerms {

    public enum Type {
	DAYS,
	PROXIMO
    }

    // -----------------------------------------------------------

    public GCshID getID();

    public int getRefcount();

    public String getName();

    public String getDescription();

    public boolean isInvisible();
    
    // ----------------------------
    
    public Type getType() throws BillTermsTypeException;

    public GCshBillTermsDays getDays();

    public GCshBillTermsProximo getProximo();

    // ----------------------------
    
    public String getParentID();

    public List<String> getChildren();

}
