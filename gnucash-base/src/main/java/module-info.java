module gnucash.base {
	requires static org.slf4j;
	requires java.desktop;
	
	requires transitive schnorxoborx.schnorxolib;

	exports org.gnucash.base.basetypes.simple;
	exports org.gnucash.base.basetypes.simple.aux;
	exports org.gnucash.base.basetypes.simple.spec;
	exports org.gnucash.base.basetypes.complex;
	exports org.gnucash.base.tuples;
}
