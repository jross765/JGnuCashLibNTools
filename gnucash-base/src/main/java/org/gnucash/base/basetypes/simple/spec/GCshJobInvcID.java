package org.gnucash.base.basetypes.simple.spec;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshJobInvcID extends GCshGenerInvcID {

    public GCshJobInvcID() {
    	super();
    }

    public GCshJobInvcID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshJobInvcID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshJobInvcID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshJobInvcID: Severe error: Copy constructor");
    	}
    }

}
