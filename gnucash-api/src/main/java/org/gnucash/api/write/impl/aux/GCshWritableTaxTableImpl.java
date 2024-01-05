package org.gnucash.api.write.impl.aux;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.impl.aux.GCshTaxTableEntryImpl;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.api.write.GnucashWritableFile;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableTaxTableImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableTaxTableImpl(
	    final GncGncTaxTable jwsdpPeer, 
	    final GnucashWritableFile gncFile) {
	super(jwsdpPeer, gncFile);
    }

    public GCshWritableTaxTableImpl(final GCshTaxTableImpl taxTab) {
	super(taxTab.getJwsdpPeer(), taxTab.getGnucashFile());
    }

    // ---------------------------------------------------------------

    @Override
    public void setName(final String name) {
	if ( name == null ) {
	    throw new IllegalArgumentException("null name given!");
	}
	
	if ( name.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty name given!");
	}

	getJwsdpPeer().setTaxtableName(name);
    }

    @Override
    public void setParentID(final GCshID prntID) {
	if ( prntID == null ) {
	    throw new IllegalArgumentException("null parent-ID given!");
	}
	
	if ( ! prntID.isSet() ) {
	    throw new IllegalArgumentException("unset parent-ID given!");
	}

	getJwsdpPeer().getTaxtableParent().setValue(prntID.toString());
    }

    @Override
    public void setParent(final GCshTaxTable prnt) {
	if ( prnt == null ) {
	    throw new IllegalArgumentException("null parent given!");
	}
	
	setParentID(prnt.getID());
    }
    
    // ---------------------------------------------------------------

    public void addEntry(final GCshTaxTableEntry entr) {
	if ( entr == null ) {
	    throw new IllegalArgumentException("null entry given!");
	}
	
	if ( ! ( entr instanceof GCshTaxTableEntryImpl ) ) {
	    throw new IllegalArgumentException("wrong implementation of tax table entry given!");
	}
	
	if ( ! entries.contains(entr) ) {
	    entries.add(entr);
	}
    }

    public void removeEntry(GCshTaxTableEntry entr) {
	if ( entr == null ) {
	    throw new IllegalArgumentException("null entry given!");
	}

	if ( ! ( entr instanceof GCshTaxTableEntryImpl ) ) {
	    throw new IllegalArgumentException("wrong implementation of tax table entry given!");
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
