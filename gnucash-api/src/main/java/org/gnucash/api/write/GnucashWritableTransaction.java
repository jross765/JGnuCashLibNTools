package org.gnucash.api.write;

import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.SplitNotFoundException;
import org.gnucash.api.read.hlp.GnucashObject;

/**
 * Transaction that can be modified.<br/>
 * For PropertyChange-Listeners we support the properties:
 * "description" and "splits".
 * 
 * @see GnucashTransaction
 */
public interface GnucashWritableTransaction extends GnucashTransaction
{

    /**
     * @param id the new currency
     * @see #setCurrencyNameSpace(String)
     * @see {@link GnucashTransaction#getCurrencyID()}
     */
    void setCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID);

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    GnucashWritableFile getWritableFile();

    /**
     * @param dateEntered the day (time is ignored) that this transaction has been
     *                    entered into the system
     * @see {@link #setDatePosted(LocalDateTime)}
     */
    void setDateEntered(final LocalDateTime dateEntered); // sic, not LocalDate

    /**
     * @param datePosted the day (time is ignored) that the money was transfered
     * @see {@link #setDateEntered(LocalDateTime)}
     */
    void setDatePosted(final LocalDate datePosted);

    void setDescription(final String desc);

    void setNumber(String string);

    /**
     * @throws SplitNotFoundException 
     *  
     * @see GnucashTransaction#getFirstSplit()
     */
    GnucashWritableTransactionSplit getWritableFirstSplit() throws SplitNotFoundException;

    /**
     * @throws SplitNotFoundException 
     * @throws 
     * @throws IllegalArgumentException 
     *  
     * @see GnucashTransaction#getSecondSplit()
     */
    GnucashWritableTransactionSplit getWritableSecondSplit() throws SplitNotFoundException;

    /**
     *  
     * @see GnucashTransaction#getSplitByID(GCshID)
     */
    GnucashWritableTransactionSplit getWritableSplitByID(GCshID id);

    /**
     *
     * @return the first split of this transaction or null.
     */
    GnucashWritableTransactionSplit getFirstSplit() throws SplitNotFoundException;

    /**
     * @return the second split of this transaction or null.
     */
    GnucashWritableTransactionSplit getSecondSplit() throws SplitNotFoundException;

    /**
     *  
     * @see GnucashTransaction#getSplits()
     */
    List<GnucashWritableTransactionSplit> getWritableSplits();

    /**
     * Create a new split, already atached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already atached to this transaction
     *  
     */
    GnucashWritableTransactionSplit createWritableSplit(GnucashAccount account);

    /**
     * Also removes the split from it's account.
     * 
     * @param impl the split to remove from this transaction
     *  
     */
    void remove(GnucashWritableTransactionSplit impl);

    /**
     * remove this transaction.
     *  
     */
    void remove();

    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered
     * for all properties.
     *
     * @param listener The PropertyChangeListener to be added
     */
    @SuppressWarnings("exports")
	void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a PropertyChangeListener for a specific property. The listener will be
     * invoked only when a call on firePropertyChange names that specific property.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The PropertyChangeListener to be added
     */
    @SuppressWarnings("exports")
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The PropertyChangeListener to be removed
     */
    @SuppressWarnings("exports")
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    @SuppressWarnings("exports")
	void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * @param name  the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link GnucashObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(final String name, final String value);

}
