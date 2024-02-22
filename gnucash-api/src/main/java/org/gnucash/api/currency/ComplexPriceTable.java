package org.gnucash.api.currency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexPriceTable implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexPriceTable.class);
    
    private static final long serialVersionUID = -3303232787168479120L;

    // ---------------------------------------------------------------

    public interface ComplexPriceTableChangeListener {
	void conversionFactorChanged(final String currency, final FixedPointNumber factor);
    }

    // -----------------------------------------------------------

    private transient volatile List<ComplexPriceTableChangeListener> listeners = null;

    // -----------------------------------------------------------

    private Map<String, SimplePriceTable> namespace2CurrTab = null;

    // -----------------------------------------------------------

    public ComplexPriceTable() {
	namespace2CurrTab = new HashMap<String, SimplePriceTable>();
	
	addForNameSpace(GCshCmdtyCurrNameSpace.CURRENCY, new SimpleCurrencyExchRateTable());
	// CAUTION: We do not / cannot add a default commodity name space as
	// in sister project JKMyMoneyLib, because we do not know in advance which
	// name spaces are going to be used.
    }

    // -----------------------------------------------------------

    public void addComplexPriceTableChangeListener(final ComplexPriceTableChangeListener listener) {
	if (listeners == null) {
	    listeners = new ArrayList<>();
	}
	listeners.add(listener);
    }

    public void removeComplexPriceTableChangeListener(final ComplexPriceTableChangeListener listener) {
	if (listeners == null) {
	    listeners = new ArrayList<>();
	}
	listeners.remove(listener);
    }

    protected void firePriceTableChanged(final String currency, final FixedPointNumber factor) {
	if (listeners != null) {
	    for (ComplexPriceTableChangeListener listener : listeners) {
		listener.conversionFactorChanged(currency, factor);
	    }
	}
    }

    // ------------------------ support for propertyChangeListeners
    /// **
    // * support for firing PropertyChangeEvents
    // * (gets initialized only if we really have listeners)
    // */
    // protected volatile PropertyChangeSupport propertyChange = null;
    //
    /// **
    // * Add a PropertyChangeListener to the listener list.
    // * The listener is registered for all properties.
    // *
    // * @param listener The PropertyChangeListener to be added
    // */
    // public final void addPropertyChangeListener(
    // final PropertyChangeListener listener) {
    // if (propertyChange == null) {
    // propertyChange = new PropertyChangeSupport(this);
    // }
    // propertyChange.addPropertyChangeListener(listener);
    // }
    //
    /// **
    // * Add a PropertyChangeListener for a specific property. The listener
    // * will be invoked only when a call on firePropertyChange names that
    // * specific property.
    // *
    // * @param propertyName The name of the property to listen on.
    // * @param listener The PropertyChangeListener to be added
    // */
    // public final void addPropertyChangeListener(
    // final String propertyName,
    // final PropertyChangeListener listener) {
    // if (propertyChange == null) {
    // propertyChange = new PropertyChangeSupport(this);
    // }
    // propertyChange.addPropertyChangeListener(propertyName, listener);
    // }
    //
    /// **
    // * Remove a PropertyChangeListener for a specific property.
    // *
    // * @param propertyName The name of the property that was listened on.
    // * @param listener The PropertyChangeListener to be removed
    // */
    // public final void removePropertyChangeListener(
    // final String propertyName,
    // final PropertyChangeListener listener) {
    // if (propertyChange != null) {
    // propertyChange.removePropertyChangeListener(propertyName, listener);
    // }
    // }
    //
    /// **
    // * Remove a PropertyChangeListener from the listener list.
    // * This removes a PropertyChangeListener that was registered
    // * for all properties.
    // *
    // * @param listener The PropertyChangeListener to be removed
    // */
    // public synchronized void removePropertyChangeListener(
    // final PropertyChangeListener listener) {
    // if (propertyChange != null) {
    // propertyChange.removePropertyChangeListener(listener);
    // }
    // }
    //
    // -------------------------------------------------------

    /**
     * Add a new nameSpace with no conversion-factors.<br/>
     * Will not overwrite an existing nameSpace.
     *
     * @param nameSpace the new nameSpace to add.
     */
    public void addForNameSpace(final String nameSpace) {
	if ( namespace2CurrTab.keySet().contains(nameSpace) ) {
	    return;
	}

	if ( nameSpace.equals(GCshCmdtyCurrNameSpace.CURRENCY) ) {
	    SimpleCurrencyExchRateTable table = new SimpleCurrencyExchRateTable();
	    table.clear();
	    addForNameSpace(nameSpace, table);
	} else {
	    SimpleCommodityQuoteTable table = new SimpleCommodityQuoteTable();
	    table.clear();
	    addForNameSpace(nameSpace, table);
	}
    }

    /**
     * Add a new nameSpace with an initial set of conversion-factors.
     *
     * @param nameSpace the new nameSpace to add.
     * @param table    an initial set of conversion-factors.
     */
    public void addForNameSpace(final String nameSpace, final SimplePriceTable table) {
	namespace2CurrTab.put(nameSpace, table);
	LOGGER.debug("addForNameSpace: Added new table for name space '" + nameSpace + "'");
    }

    // ---------------------------------------------------------------
    
    /**
     * @see SimplePriceTable#setConversionFactor(java.lang.String, FixedPointNumber)
     */
    public FixedPointNumber getConversionFactor(final String nameSpace, final String code) {
    
        if (code == null) {
            throw new IllegalArgumentException("null code!");
        }
    
        SimplePriceTable table = getByNamespace(nameSpace);
        if (table == null) {
            return null;
        }
    
        return table.getConversionFactor(code);
    }

    /**
     * If the nameSpace does not exist yet, it is created.
     *
     * @see SimplePriceTable#setConversionFactor(java.lang.String, FixedPointNumber)
     */
    public void setConversionFactor(final String nameSpace, final String code, 
	                            final FixedPointNumber pFactor) {

	if (code == null) {
	    throw new IllegalArgumentException("null code given!");
	}
	if (pFactor == null) {
	    throw new IllegalArgumentException("null conversion-factor given!");
	}

	SimplePriceTable table = getByNamespace(nameSpace);
	if (table == null) {
	    addForNameSpace(nameSpace);
	    table = getByNamespace(nameSpace);
	}

	table.setConversionFactor(code, pFactor);

	firePriceTableChanged(code, pFactor);
    }

    public void setConversionFactor(final GCshCmdtyCurrID cmdtyCurrID, final FixedPointNumber pFactor) {
	if (cmdtyCurrID == null) {
	    throw new IllegalArgumentException("null code given!");
	}
	
	setConversionFactor(cmdtyCurrID.getNameSpace(), cmdtyCurrID.getCode(),
		            pFactor);
    }
    
    // ---------------------------------------------------------------
    
    /**
     * @see SimplePriceTable#convertFromBaseCurrency(FixedPointNumber,
     *      java.lang.String)
     */
    public boolean convertFromBaseCurrency(FixedPointNumber pValue, 
	    final GCshCmdtyCurrID cmdtyCurrID) {

	SimplePriceTable table = getByNamespace(cmdtyCurrID.getNameSpace());
	if (table == null) {
	    return false;
	}

	return table.convertFromBaseCurrency(pValue, cmdtyCurrID.getCode());
    }

    public boolean convertToBaseCurrency(FixedPointNumber pValue, 
	    final GCshCmdtyCurrID cmdtyCurrID) {

	SimplePriceTable table = getByNamespace(cmdtyCurrID.getNameSpace());

	if (table == null) {
	    return false;
	}

	return table.convertToBaseCurrency(pValue, cmdtyCurrID.getCode());
    }

    // ---------------------------------------------------------------
    
    public Collection<String> getNameSpaces() {
	return namespace2CurrTab.keySet();
    }

    /**
     * @param nameSpace
     * @return
     */
    protected SimplePriceTable getByNamespace(final String nameSpace) {
	return namespace2CurrTab.get(nameSpace);
    }

    /**
     * @param nameSpace
     */
    public Collection<String> getCurrencies(final String nameSpace) {
	SimplePriceTable table = getByNamespace(nameSpace);
	if (table == null) {
	    return new HashSet<String>();
	}
	return table.getCurrencies();
    }

    // ---------------------------------------------------------------

    /**
     * @see SimplePriceTable#clear()
     */
    public void clear() {
        for ( String nameSpace : namespace2CurrTab.keySet() ) {
            namespace2CurrTab.get(nameSpace).clear();
        }
        
        namespace2CurrTab.clear();
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	String result = "ComplexPriceTable [\n";
	
	for ( String nameSpace : getNameSpaces() ) {
	    result += "=======================================\n";
	    result += "Name space: " + nameSpace + "\n";
	    result += "=======================================\n";
	    result += getByNamespace(nameSpace).toString() + "\n";
	}

	result += "]";
	
	return result;
    }
    
}
