module gnucash.apiext {
	requires static org.slf4j;
	// requires java.desktop;
	
	requires gnucash.api;

	exports org.gnucash.apiext.depot;
}