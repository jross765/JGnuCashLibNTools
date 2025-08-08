package org.gnucash.base.basetypes.simple.spec;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshCustInvcEntrID extends GCshGenerInvcEntrID {

    public GCshCustInvcEntrID() {
    	super();
    }

    public GCshCustInvcEntrID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshCustInvcEntrID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshCustInvcEntrID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshCustInvcEntrID: Severe error: Copy constructor");
    	}
    }

}
