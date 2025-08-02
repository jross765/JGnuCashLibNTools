package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshCustID extends GCshID {

    public GCshCustID() {
    	super();
    }

    public GCshCustID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshCustID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshCustID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshCustID: Severe error: Copy constructor");
    	}
    }

    // ---------------------------------------------------------------
    
    public GCshID getRawID() throws InvalidGCshIDException, GCshIDNotSetException {
    	return new GCshID(super.get());
    }

}
