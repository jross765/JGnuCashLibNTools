package org.gnucash.api.write;

import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashObject;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.SplitNotFoundException;

/**
 * Transaction that can be modified.<br/>
 * For PropertyChange-Listeners we support the properties:
 * "description" and "splits".
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getFirstSplit()
     */
    GnucashWritableTransactionSplit getWritableFirstSplit() throws SplitNotFoundException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getSecondSplit()
     */
    GnucashWritableTransactionSplit getWritableSecondSplit() throws SplitNotFoundException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getSplitByID(GCshID)
     */
    GnucashWritableTransactionSplit getWritableSplitByID(GCshID id) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     *
     * @return the first split of this transaction or null.
     */
    GnucashWritableTransactionSplit getFirstSplit() throws SplitNotFoundException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @return the second split of this transaction or null.
     */
    GnucashWritableTransactionSplit getSecondSplit() throws SplitNotFoundException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getSplits()
     */
    List<GnucashWritableTransactionSplit> getWritableSplits() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * Create a new split, already atached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already atached to this transaction
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    GnucashWritableTransactionSplit createWritableSplit(GnucashAccount account) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * Also removes the split from it's account.
     * 
     * @param impl the split to remove from this transaction
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void remove(GnucashWritableTransactionSplit impl) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * remove this transaction.
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void remove() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered
     * for all properties.
     *
     * @param listener The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a PropertyChangeListener for a specific property. The listener will be
     * invoked only when a call on firePropertyChange names that specific property.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * @param name  the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link GnucashObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(final String name, final String value);

}
