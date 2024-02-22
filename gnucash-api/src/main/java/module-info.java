module gnucash.api {
	requires static org.slf4j;
	requires java.desktop;
	requires jakarta.xml.bind;
	requires java.xml;
	
	requires gnucash.base;
	
	exports org.gnucash.api.currency;
	
	exports org.gnucash.api.read;
	exports org.gnucash.api.read.aux;
	// exports org.gnucash.api.read.hlp;
	exports org.gnucash.api.read.spec;
	exports org.gnucash.api.read.impl;
	exports org.gnucash.api.read.impl.aux;
	// exports org.gnucash.api.read.impl.hlp;
	exports org.gnucash.api.read.impl.spec;
	
	exports org.gnucash.api.write;
	exports org.gnucash.api.write.aux;
	// exports org.gnucash.api.write.hlp;
	exports org.gnucash.api.write.spec;
	exports org.gnucash.api.write.impl;
	exports org.gnucash.api.write.impl.aux;
	// exports org.gnucash.api.write.impl.hlp;
	exports org.gnucash.api.write.impl.spec;
}