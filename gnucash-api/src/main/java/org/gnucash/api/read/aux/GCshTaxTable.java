package org.gnucash.api.read.aux;

import java.util.List;

import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;


/**
 * Tax tables can used to determine the tax for customer invoices or vendor bills. 
 * <br>
 * Cf. <a href="https://cvs.gnucash.org/docs/C/gnucash-guide/bus-setuptaxtables.html">GnuCash manual</a>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ar-setup1.html#busnss-ar-setuptaxtables">GnuCash manual</a>
 * 
 * @see GnuCashCustomerInvoice
 * @see GnuCashVendorBill
 */
public interface GCshTaxTable {

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshTaxTabID getID();

    /**
     *
     * @return the name the user gave to this job.
     */
    String getName();

    /**
     * @return 
     * @see GCshTaxTable#isInvisible()
     */
    boolean isInvisible();
    
    // ---------------------------------------------------------------

    /**
     * @return id of the parent-taxtable
     */
    GCshTaxTabID getParentID();

    /**
     * @return the parent-taxtable
     */
    GCshTaxTable getParent();

    /**
     * @return the entries in this tax-table
     */
    List<GCshTaxTableEntry> getEntries();

}
