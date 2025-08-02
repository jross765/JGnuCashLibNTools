package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshGenerInvcEntrID extends GCshID {

    public GCshGenerInvcEntrID() {
    	super();
    }

    public GCshGenerInvcEntrID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshGenerInvcEntrID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshGenerInvcEntrID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshGenerInvcEntrID: Severe error: Copy constructor");
    	}
    }

}
