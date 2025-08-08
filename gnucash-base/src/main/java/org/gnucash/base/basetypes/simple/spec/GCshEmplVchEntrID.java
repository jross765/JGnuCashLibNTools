package org.gnucash.base.basetypes.simple.spec;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshEmplVchEntrID extends GCshGenerInvcEntrID {

    public GCshEmplVchEntrID() {
    	super();
    }

    public GCshEmplVchEntrID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshEmplVchEntrID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshEmplVchEntrID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshEmplVchEntrID: Severe error: Copy constructor");
    	}
    }

}
