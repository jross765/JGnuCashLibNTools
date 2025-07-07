package org.gnucash.api.read.impl.aux;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnuCashTaxTable that uses JWSDP.
 * 
 * @see GCshTaxTable
 */
public class GCshTaxTableImpl implements GCshTaxTable {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshTaxTableImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncTaxTable jwsdpPeer;

    /**
     * the file we belong to.
     */
    protected final GnuCashFile myFile;
    
    // ----------------------------

    /**
     * @see #getEntries()
     */
    protected List<GCshTaxTableEntry> entries = null;

    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GCshTaxTableImpl(
	    final GncGncTaxTable peer, 
	    final GnuCashFile gcshFile) {
	super();
	
	this.jwsdpPeer = peer;
	this.myFile = gcshFile;
    }

    // ---------------------------------------------------------------

    /**
     *
     * @return The JWSDP-Object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncGncTaxTable getJwsdpPeer() {
	return jwsdpPeer;
    }

    public GnuCashFile getGnuCashFile() {
	return myFile;
    }

    // ---------------------------------------------------------------

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    @Override
    public GCshID getID() {
	assert jwsdpPeer.getTaxtableGuid().getType().equals(Const.XML_DATA_TYPE_GUID);

	String guid = jwsdpPeer.getTaxtableGuid().getValue();
	if (guid == null) {
	    throw new IllegalStateException(
		    "taxtable has a null guid-value! guid-type=" + jwsdpPeer.getTaxtableGuid().getType());
	}

	return new GCshID(guid);
    }

    /**
     * @see GCshTaxTable#getName()
     */
    @Override
    public String getName() {
	return jwsdpPeer.getTaxtableName();
    }

    /**
     * @see GCshTaxTable#isInvisible()
     */
    @Override
    public boolean isInvisible() {
	return jwsdpPeer.getTaxtableInvisible() != 0;
    }

    /**
     * @see GCshTaxTable#getParentID()
     */
    @Override
    public GCshID getParentID() {
	GncGncTaxTable.TaxtableParent parent = jwsdpPeer.getTaxtableParent();
	if (parent == null) {
	    return null;
	}
	return new GCshID( parent.getValue() );
    }

    /**
     * @see GCshTaxTable#getParent()
     * @return the parent tax-table or null
     */
    @Override
    public GCshTaxTable getParent() {
	return myFile.getTaxTableByID(getParentID());
    }

    /**
     * @see GCshTaxTable#getEntries()
     * @return all entries to this tax-table
     */
    @Override
	public List<GCshTaxTableEntry> getEntries() {
		if ( entries == null ) {
			GncGncTaxTable.TaxtableEntries jwsdpEntries = getJwsdpPeer().getTaxtableEntries();
			entries = new ArrayList<GCshTaxTableEntry>();
			for ( GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry element : jwsdpEntries.getGncGncTaxTableEntry() ) {
				entries.add(new GCshTaxTableEntryImpl(element, myFile));
			}

		}

		return entries;
	}

    // ---------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();

	buffer.append("GCshTaxTableImpl [\n");

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
