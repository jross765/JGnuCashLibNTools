package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshAcctID extends GCshID {

    public GCshAcctID() {
    	super();
    }

    public GCshAcctID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshAcctID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshAcctID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshAcctID: Severe error: Copy constructor");
    	}
    }

}
