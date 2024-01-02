package org.gnucash.api.write.aux;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.aux.GCshTaxTableEntry;

public interface GCshWritableTaxTableEntry extends GCshTaxTableEntry {

    void setType(final Type type);

    void setTypeStr(final String typeStr);

    void setAccountID(final GCshID acctID);

    void setAccount(final GnucashAccount acct);

    void setAmount(final FixedPointNumber amt);
}
