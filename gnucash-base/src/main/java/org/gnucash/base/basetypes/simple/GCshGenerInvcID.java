package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshGenerInvcID extends GCshID {

    public GCshGenerInvcID() {
    	super();
    }

    public GCshGenerInvcID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshGenerInvcID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshGenerInvcID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshGenerInvcID: Severe error: Copy constructor");
    	}
    }

}
