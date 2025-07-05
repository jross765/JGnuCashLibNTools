package org.gnucash.api.read.hlp;

import java.util.List;

public interface HasUserDefinedAttributes {

    String getUserDefinedAttribute(String name);
    
    List<String> getUserDefinedAttributeKeys();
    
}
