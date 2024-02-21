module gnucash.base {
	requires static org.slf4j;
	requires java.desktop;
	
	exports org.gnucash.base.basetypes.simple;
	exports org.gnucash.base.basetypes.complex;
}
