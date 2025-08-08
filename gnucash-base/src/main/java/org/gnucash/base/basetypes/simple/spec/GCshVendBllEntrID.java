package org.gnucash.base.basetypes.simple.spec;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshVendBllEntrID extends GCshGenerInvcEntrID {

    public GCshVendBllEntrID() {
    	super();
    }

    public GCshVendBllEntrID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshVendBllEntrID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshVendBllEntrID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshVendBllEntrID: Severe error: Copy constructor");
    	}
    }

}
