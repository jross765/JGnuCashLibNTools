package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshSpltID extends GCshID {

    public GCshSpltID() {
    	super();
    }

    public GCshSpltID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshSpltID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshSpltID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshSpltID: Severe error: Copy constructor");
    	}
    }

}
