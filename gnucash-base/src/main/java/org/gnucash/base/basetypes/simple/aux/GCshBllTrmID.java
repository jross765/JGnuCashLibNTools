package org.gnucash.base.basetypes.simple.aux;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshBllTrmID extends GCshID {

    public GCshBllTrmID() {
    	super();
    }

    public GCshBllTrmID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshBllTrmID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshBllTrmID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshBllTrmID: Severe error: Copy constructor");
    	}
    }

}
