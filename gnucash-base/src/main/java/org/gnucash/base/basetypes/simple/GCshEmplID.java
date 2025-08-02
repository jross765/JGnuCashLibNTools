package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshEmplID extends GCshID {

    public GCshEmplID() {
    	super();
    }

    public GCshEmplID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshEmplID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshEmplID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshEmplID: Severe error: Copy constructor");
    	}
    }

}
