package org.gnucash.api.currency;

import java.util.Collection;

import org.gnucash.api.numbers.FixedPointNumber;

public interface SimplePriceTable {

    public FixedPointNumber getConversionFactor(final String code);

    public void setConversionFactor(final String code, final FixedPointNumber factor);

    // ---------------------------------------------------------------

    public boolean convertFromBaseCurrency(FixedPointNumber value, final String code);

    public boolean convertToBaseCurrency(FixedPointNumber value, final String code);

    // ---------------------------------------------------------------

    public Collection<String> getCurrencies();

    public void clear();

}
