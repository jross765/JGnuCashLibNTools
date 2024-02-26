package org.gnucash.api.write.hlp;

import org.gnucash.api.read.hlp.HasUserDefinedAttributes;

public interface HasWritableUserDefinedAttributes extends HasUserDefinedAttributes {

    void setUserDefinedAttribute(String name, String value);
    
}
