package org.gnucash.base.basetypes.simple;

import java.util.UUID;

public class GCshPrcID extends GCshID {

    public GCshPrcID() {
    	super();
    }

    public GCshPrcID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshPrcID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshPrcID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshPrcID: Severe error: Copy constructor");
    	}
    }

}
