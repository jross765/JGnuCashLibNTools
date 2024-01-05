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
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
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
		return (Integer.valueOf(getType(aO1)).compareTo(Integer.valueOf(getType(aO2))));
	}

	/**
	 * Return an integer for the type of entry.
	 * This is the primary ordering used.
	 * @param element the object to examine
	 * @return int > 0
	 */
	private int getType(final Object element) {
		if (element instanceof GncCommodity) {
			return 1;
		} else if (element instanceof GncV2.GncBook.GncPricedb) {
			return 2;
		} else if (element instanceof GncAccount) {
			return 3;
		} else if (element instanceof GncBudget) {
			return 4;
		} else if (element instanceof GncTransaction) {
			return 5;
		} else if (element instanceof GncV2.GncBook.GncTemplateTransactions) {
			return 6;
		} else if (element instanceof GncV2.GncBook.GncSchedxaction) {
			return 7;
		} else if (element instanceof GncGncJob) {
			return 8;
		} else if (element instanceof GncGncTaxTable) {
			return 9;
		} else if (element instanceof GncGncInvoice) {
			return 10;
		} else if (element instanceof GncGncCustomer) {
			return 11;
		} else if (element instanceof GncGncEmployee) {
			return 12;
		} else if (element instanceof GncGncEntry) {
			return 13;
		} else if (element instanceof GncGncBillTerm) {
			return 14;
		} else if (element instanceof GncGncVendor) {
			return 15;
		} else if (element instanceof GncV2.GncBook.GncPricedb.Price) {
			return 16;
		} else {
			throw new IllegalStateException("Unexpected element in GNC:Book found! <" + element.toString() + ">");
		}
	}
}
