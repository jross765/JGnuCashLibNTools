package org.gnucash.read.spec;

import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.GnucashGenerJob;

public interface GnucashEmployeeJob extends GnucashGenerJob {

	/**
	 *
	 * @return the customer this job is from.
	 */
	GnucashEmployee getEmployee();

	/**
	 *
	 * @return the id of the customer this job is from.
	 * @see #getEmployee()
	 */
	String getEmployeeId();
	
}
