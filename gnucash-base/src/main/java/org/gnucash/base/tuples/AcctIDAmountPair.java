package org.gnucash.base.tuples;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

public record AcctIDAmountPair(GCshID accountID, FixedPointNumber amount) {

	private final static double UNSET_VALUE = -999999;
	
	// ---------------------------------------------------------------
	
	public boolean isNotNull() {
		if ( accountID == null)
			return false;
		
		if ( amount == null)
			return false;
		
		return true;
	}

	public boolean isSet() {
		return accountID.isSet() && ( amount.doubleValue() != UNSET_VALUE );
	}

}
