package org.gnucash.api.read.aux;

import java.util.List;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashVendorBill;

/**
 * Billing Terms can be used to determine the payment due date and be a guide for determining discount for early 
 * payment of invoices or vendor bills.
 * <br>  
 * Cf. <a href="https://cvs.gnucash.org/docs/C/gnucash-guide/bus-setupterms.html">GnuCash manual</a>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ar-setup1.html#busnss-ar-setupterms">GnuCash manual</a>
 * 
 * @see GnucashCustomerInvoice
 * @see GnucashVendorBill
 */
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
