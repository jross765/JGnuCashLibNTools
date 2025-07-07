package org.gnucash.api.write.impl.aux;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.impl.aux.GCshTaxTableEntryImpl;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableTaxTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshTaxTableImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableTaxTableImpl extends GCshTaxTableImpl 
                                      implements GCshWritableTaxTable 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableTaxTableImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableTaxTableImpl(
	    final GncGncTaxTable jwsdpPeer, 
	    final GnuCashWritableFile gcshFile) {
	super(jwsdpPeer, gcshFile);
    }

    public GCshWritableTaxTableImpl(final GCshTaxTableImpl taxTab) {
	super(taxTab.getJwsdpPeer(), taxTab.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    @Override
    public void setName(final String name) {
	if ( name == null ) {
	    throw new IllegalArgumentException("argument <name> is null");
	}
	
	if ( name.trim().length() == 0 ) {
	    throw new IllegalArgumentException("argument <name> is empty");
	}

	getJwsdpPeer().setTaxtableName(name);
    }

    @Override
    public void setParentID(final GCshID prntID) {
	if ( prntID == null ) {
	    throw new IllegalArgumentException("argument <prntID> is null");
	}
	
	if ( ! prntID.isSet() ) {
	    throw new IllegalArgumentException("argument <prntID> is not set");
	}

	getJwsdpPeer().getTaxtableParent().setValue(prntID.toString());
    }

    @Override
    public void setParent(final GCshTaxTable prnt) {
	if ( prnt == null ) {
	    throw new IllegalArgumentException("argument <prnt> is null");
	}
	
	setParentID(prnt.getID());
    }
    
    // ---------------------------------------------------------------

    public void addEntry(final GCshTaxTableEntry entr) {
	if ( entr == null ) {
	    throw new IllegalArgumentException("argument <entr> is null");
	}
	
	if ( ! ( entr instanceof GCshTaxTableEntryImpl ) ) {
	    throw new IllegalArgumentException("argument <entr> is not instance of GCshTaxTableEntryImpl");
	}
	
	if ( ! entries.contains(entr) ) {
	    entries.add(entr);
	}
    }

    public void removeEntry(GCshTaxTableEntry entr) {
	if ( entr == null ) {
	    throw new IllegalArgumentException("argument <entr> is null");
	}

	if ( ! ( entr instanceof GCshTaxTableEntryImpl ) ) {
	    throw new IllegalArgumentException("argument <entr> is not instance of GCshTaxTableEntryImpl");
	}
	
	for ( GCshTaxTableEntry elt : entries ) {
	    if ( elt.equals(entr) ) {
		entries.remove(elt);
		return;
	    }
	}
    }

    // ---------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();

	buffer.append("GCshWritableTaxTableImpl [\n");

	buffer.append("id=");
	buffer.append(getID());

	buffer.append(", name='");
	buffer.append(getName() + "'");

	buffer.append(", parent-id=");
	buffer.append(getParentID() + "\n");

	buffer.append("  Entries:\n");
	for (GCshTaxTableEntry entry : getEntries()) {
	    buffer.append("   - " + entry + "\n");
	}

	buffer.append("]\n");

	return buffer.toString();
    }
}
