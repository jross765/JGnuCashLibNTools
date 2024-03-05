package org.gnucash.api.write.aux;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.aux.GCshTaxTableEntry;

public interface GCshWritableTaxTableEntry extends GCshTaxTableEntry {

    void setType(Type type);

    void setTypeStr(String typeStr);

    void setAccountID(GCshID acctID);

    void setAccount(GnuCashAccount acct);

    void setAmount(FixedPointNumber amt);
}
