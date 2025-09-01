package org.gnucash.api.write.impl.hlp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnuCashObjectImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableObjectImpl extends GnuCashObjectImpl
		                               implements GnuCashWritableObject 
{

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableObjectImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("unused")
	private Object obj;

	/**
	 * support for firing PropertyChangeEvents. (gets initialized only if we really
	 * have listeners)
	 */
	private volatile PropertyChangeSupport myPtyChg = null;

	// ---------------------------------------------------------------

	public GnuCashWritableObjectImpl(final GnuCashWritableFile myFile) {
		super(myFile);
	}

	/**
	 * @param myFile 
	 * @param obj the object we are helping with
	 */
	public GnuCashWritableObjectImpl(final GnuCashWritableFile myFile, final Object obj) {
		super(myFile);
		this.obj = obj;
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public GnuCashWritableFile getWritableGnuCashFile() {
		return (GnuCashWritableFile) getGnuCashFile();
	}

	// ---------------------------------------------------------------

	/**
	 * Returned value may be null if we never had listeners.
	 *
	 * @return Our support for firing PropertyChangeEvents
	 */
	public PropertyChangeSupport getPropertyChangeSupport() {
		return myPtyChg;
	}

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPtyChg == null ) {
			myPtyChg = new PropertyChangeSupport(this);
		}
		myPtyChg.addPropertyChangeListener(listener);
	}

	/**
	 * Add a PropertyChangeListener for a specific property. The listener will be
	 * invoked only when a call on firePropertyChange names that specific property.
	 *
	 * @param ptyName  The name of the property to listen on.
	 * @param listener The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(final String ptyName, final PropertyChangeListener listener) {
		if ( myPtyChg == null ) {
			myPtyChg = new PropertyChangeSupport(this);
		}
		myPtyChg.addPropertyChangeListener(ptyName, listener);
	}

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 *
	 * @param ptyName  The name of the property that was listened on.
	 * @param listener The PropertyChangeListener to be removed
	 */
	public final void removePropertyChangeListener(final String ptyName, final PropertyChangeListener listener) {
		if ( myPtyChg != null ) {
			myPtyChg.removePropertyChangeListener(ptyName, listener);
		}
	}

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed
	 */
	public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPtyChg != null ) {
			myPtyChg.removePropertyChangeListener(listener);
		}
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		return "GnuCashWritableObjectImpl@" + hashCode();
	}

}
