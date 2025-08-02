package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshVendID extends GCshID {

    public GCshVendID() {
    	super();
    }

    public GCshVendID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshVendID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshVendID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshVendID: Severe error: Copy constructor");
    	}
    }
    
    // ---------------------------------------------------------------
    
    public GCshID getRawID() throws InvalidGCshIDException, GCshIDNotSetException {
    	return new GCshID(super.get());
    }

}
