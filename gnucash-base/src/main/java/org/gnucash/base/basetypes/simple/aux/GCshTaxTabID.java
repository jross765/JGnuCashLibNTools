package org.gnucash.base.basetypes.simple.aux;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshTaxTabID extends GCshID {

    public GCshTaxTabID() {
    	super();
    }

    public GCshTaxTabID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshTaxTabID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshTaxTabID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshTaxTabID: Severe error: Copy constructor");
    	}
    }

}
