package org.gnucash.currency;

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.gnucash.basetypes.complex.GCshCmdtyID;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCommodityQuoteTable implements SimplePriceTable,
                                                 Serializable 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCommodityQuoteTable.class);

    private static final long serialVersionUID = -1023793498877236292L;

    // -----------------------------------------------------------

    /**
     * maps a currency-name in capital letters(e.g. "GBP") to a factor
     * {@link FixedPointNumber} that is to be multiplied with an amount of that
     * currency to get the value in the base-currency.
     *
     * @see {@link #getConversionFactor(String)}
     */
    private Map<String, FixedPointNumber> mCmdtyID2Factor = null;

    // -----------------------------------------------------------

    public SimpleCommodityQuoteTable() {
	mCmdtyID2Factor = new Hashtable<String, FixedPointNumber>();
    }

    // -----------------------------------------------------------

    /**
     * @param cmdtyID a currency-name in capital letters(e.g. "GBP")
     * @return a factor {@link FixedPointNumber} that is to be multiplied with an
     *         amount of that currency to get the value in the base-currency.
     */
    @Override
    public FixedPointNumber getConversionFactor(final String cmdtyID) {
	return mCmdtyID2Factor.get(cmdtyID);
    }

    /**
     * @param cmdtyID a currency-name in capital letters(e.g. "GBP")
     * @param factor              a factor {@link FixedPointNumber} that is to be
     *                            multiplied with an amount of that currency to get
     *                            the value in the base-currency.
     */
    @Override
    public void setConversionFactor(final String cmdtyQualifID, final FixedPointNumber factor) {
	mCmdtyID2Factor.put(cmdtyQualifID, factor);
    }

    public void setConversionFactor(final GCshCmdtyID cmdtyID, final FixedPointNumber factor) {
	mCmdtyID2Factor.put(cmdtyID.toString(), factor);
    }

    public void setConversionFactor(final String nameSpace, final String code, 
	                            final FixedPointNumber factor) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	mCmdtyID2Factor.put(new GCshCmdtyID(nameSpace, code).toString(), factor);
    }

    // ---------------------------------------------------------------

    /**
     * @param value               the value to convert
     * @param cmdtyID the currency to convert to
     * @return false if the conversion is not possible
     */
    @Override
    public boolean convertFromBaseCurrency(FixedPointNumber value, final String cmdtyID) {
        FixedPointNumber factor = getConversionFactor(cmdtyID);
        if (factor == null) {
            return false;
        }
        value.divideBy(factor);
        return true;
    }

    /**
     * @param value           the value to convert
     * @param cmdtyID it's currency
     * @return false if the conversion is not possible
     */
    @Override
    public boolean convertToBaseCurrency(FixedPointNumber value, final String cmdtyID) {
	FixedPointNumber factor = getConversionFactor(cmdtyID);
	if (factor == null) {
	    return false;
	}
	value.multiply(factor);
	return true;
    }

    // ---------------------------------------------------------------

    /**
     * @return all currency-names
     */
    @Override
    public Collection<String> getCurrencies() {
	return mCmdtyID2Factor.keySet();
    }
    
    /**
     * forget all conversion-factors.
     */
    @Override
    public void clear() {
        mCmdtyID2Factor.clear();
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	String result = "[SimpleCommodityQuoteTable:\n";
	
	result += "No. of entries: " + mCmdtyID2Factor.size() + "\n";
	
	result += "Entries:\n";
	for ( String cmdtyID : mCmdtyID2Factor.keySet() ) {
	    // result += " - " + cmdtyID + "\n";
	    result += " - " + cmdtyID + ";" + mCmdtyID2Factor.get(cmdtyID) + "\n";
	}
	
	result += "]";
	
	return result;
    }

}
