package org.gnucash.api.write.impl.hlp;

import java.util.Comparator;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncBudget;
import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.GncSchedxaction;
import org.gnucash.api.generated.GncTemplateTransactions;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sorter for the elements in a Gnc:Book.
 */
public class BookElementsSorter implements Comparator<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookElementsSorter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Object aO1, final Object aO2) {
	// no secondary sorting
	return (Integer.valueOf(getTypeOrder(aO1)).compareTo(Integer.valueOf(getTypeOrder(aO2))));
    }

    // Return an integer for the type of entry. This is the primary ordering used.
    // 
    // The order numbers defined in this function do not absolutely have to be
    // defined as they have been -- it just makes things easier as this is
    // how GnuCash normally stores them (it can handle other variants as well,
    // however).
    private int getTypeOrder(final Object element) {
	if (element instanceof GncCommodity) {
	    return 1;
	} else if (element instanceof GncPricedb) {
	    return 2;
	} else if (element instanceof GncAccount) {
	    return 3;
	} else if (element instanceof GncBudget) {
	    return 4;
	} else if (element instanceof GncTransaction) {
	    return 5;
	} else if (element instanceof GncTemplateTransactions) {
	    return 6;
	} else if (element instanceof GncSchedxaction) {
	    return 7;
	} else if (element instanceof GncGncBillTerm) {
	    return 8;
	} else if (element instanceof GncGncCustomer) {
	    return 9;
	} else if (element instanceof GncGncEmployee) {
	    return 10;
	} else if (element instanceof GncGncEntry) {
	    return 11;
	} else if (element instanceof GncGncInvoice) {
	    return 12;
	} else if (element instanceof GncGncJob) {
	    return 13;
	} else if (element instanceof GncGncTaxTable) {
	    return 14;
	} else if (element instanceof GncGncVendor) {
	    return 15;
	} else if (element instanceof Price) {
	    return 16;
	} else {
	    throw new IllegalStateException("Unexpected element in GNC:Book found! <" + element.toString() + ">");
	}
    }
}
