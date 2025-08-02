package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshTrxID extends GCshID {

    public GCshTrxID() {
    	super();
    }

    public GCshTrxID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshTrxID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshTrxID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshTrxID: Severe error: Copy constructor");
    	}
    }

}
