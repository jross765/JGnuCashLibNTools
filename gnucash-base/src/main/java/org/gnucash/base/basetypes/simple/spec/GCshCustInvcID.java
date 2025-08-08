package org.gnucash.base.basetypes.simple.spec;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshCustInvcID extends GCshGenerInvcID {

    public GCshCustInvcID() {
    	super();
    }

    public GCshCustInvcID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshCustInvcID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshCustInvcID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshCustInvcID: Severe error: Copy constructor");
    	}
    }

}
