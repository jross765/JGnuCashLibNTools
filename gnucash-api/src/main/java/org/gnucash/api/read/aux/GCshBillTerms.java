package org.gnucash.api.read.aux;

import java.util.List;

import org.gnucash.api.basetypes.simple.GCshID;

public interface GCshBillTerms {

    public enum Type {
	DAYS,
	PROXIMO,
	
	UNSET
    }

    // -----------------------------------------------------------

    GCshID getID();

    int getRefcount();

    String getName();

    String getDescription();

    boolean isInvisible();

    // ----------------------------

    Type getType() throws BillTermsTypeException;

    GCshBillTermsDays getDays();

    GCshBillTermsProximo getProximo();

    // ----------------------------

    GCshID getParentID();

    List<String> getChildren();

}
