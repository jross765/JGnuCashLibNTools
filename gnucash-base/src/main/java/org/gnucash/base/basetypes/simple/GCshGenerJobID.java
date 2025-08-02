package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshGenerJobID extends GCshID {

    public GCshGenerJobID() {
    	super();
    }

    public GCshGenerJobID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshGenerJobID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshGenerJobID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshGenerJobID: Severe error: Copy constructor");
    	}
    }

}
