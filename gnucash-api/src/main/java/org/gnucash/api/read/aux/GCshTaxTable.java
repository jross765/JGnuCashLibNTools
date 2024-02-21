package org.gnucash.api.read.aux;

import java.util.Collection;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashVendorBill;


/**
 * Tax tables can used to determine the tax for customer invoices or vendor bills. 
 * <br>
 * Cf. <a href="https://cvs.gnucash.org/docs/C/gnucash-guide/bus-setuptaxtables.html">GnuCash manual</a>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ar-setup1.html#busnss-ar-setuptaxtables">GnuCash manual</a>
 * 
 * @see GnucashCustomerInvoice
 * @see GnucashVendorBill
 */
public interface GCshTaxTable {

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getID();

    /**
     *
     * @return the name the user gave to this job.
     */
    String getName();

    /**
     * @see GCshTaxTable#isInvisible()
     */
    boolean isInvisible();
    
    // ---------------------------------------------------------------

    /**
     * @return id of the parent-taxtable
     */
    GCshID getParentID();

    /**
     * @return the parent-taxtable
     */
    GCshTaxTable getParent();

    /**
     * @return the entries in this tax-table
     */
    Collection<GCshTaxTableEntry> getEntries();

}
