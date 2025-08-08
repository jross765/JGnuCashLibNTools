package org.gnucash.base.basetypes.simple.spec;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshVendBllID extends GCshGenerInvcID {

    public GCshVendBllID() {
    	super();
    }

    public GCshVendBllID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshVendBllID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshVendBllID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshVendBllID: Severe error: Copy constructor");
    	}
    }

}
