package org.gnucash.api.write.hlp;

import org.gnucash.api.read.hlp.HasUserDefinedAttributes;

public interface HasWritableUserDefinedAttributes extends HasUserDefinedAttributes {

    void addUserDefinedAttribute(String type, String name, String value);
    
    void removeUserDefinedAttribute(String name);
    
    void setUserDefinedAttribute(String name, String value);
    
}
