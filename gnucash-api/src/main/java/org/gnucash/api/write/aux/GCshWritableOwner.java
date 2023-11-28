package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshOwner;

public interface GCshWritableOwner extends GCshOwner {

	public void setJIType(JIType jiType);

	public void setInvcType(String invcType);

}
