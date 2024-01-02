package org.gnucash.api.write.impl.aux;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.impl.aux.GCshTaxTableEntryImpl;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
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
    public GCshWritableTaxTableImpl(final GncV2.GncBook.GncGncTaxTable jwsdpPeer, final GnucashFile gncFile) {
	super(jwsdpPeer, gncFile);
    }

    public GCshWritableTaxTableImpl(GCshTaxTableImpl taxTab) {
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

    public void addEntry(final GCshTaxTableEntryImpl entr) {
	if ( entr == null ) {
	    throw new IllegalArgumentException("null entry given!");
	}
	
	if ( ! entries.contains(entr) ) {
	    entries.add(entr);
	}
    }

    public void removeEntry(GCshTaxTableEntryImpl entr) {
	if ( entr == null ) {
	    throw new IllegalArgumentException("null entry given!");
	}

	for ( GCshTaxTableEntry elt : entries ) {
	    if ( elt.equals(entr) ) {
		entries.remove(elt);
		return;
	    }
	}
    }

}
