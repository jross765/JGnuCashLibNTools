package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorJobImpl;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileJobManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager.class);
    
    // ---------------------------------------------------------------
    
    protected GnucashFileImpl gcshFile;

    protected Map<GCshID, GnucashGenerJob> jobMap;

    // ---------------------------------------------------------------
    
	public FileJobManager(GnucashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		jobMap = new HashMap<GCshID, GnucashGenerJob>();

		for ( Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext(); ) {
			Object bookElement = iter.next();
			if ( !(bookElement instanceof GncGncJob) ) {
				continue;
			}
			GncGncJob jwsdpJob = (GncGncJob) bookElement;

			try {
				GnucashGenerJobImpl generJob = createGenerJob(jwsdpJob);
				GCshID jobID = generJob.getID();
				if ( jobID == null ) {
					LOGGER.error("init: File contains a (generic) Job w/o an ID. indexing it with the Null-ID '"
							+ GCshID.NULL_ID + "'");
					jobID = new GCshID(GCshID.NULL_ID);
				}
				jobMap.put(jobID, generJob);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal (generic) Job entry with id=" + jwsdpJob.getJobId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in generic Job map: " + jobMap.size());
	}

	protected GnucashGenerJobImpl createGenerJob(final GncGncJob jwsdpJob) {
		GnucashGenerJobImpl job = new GnucashGenerJobImpl(jwsdpJob, gcshFile);
		LOGGER.debug("Generated new generic job: " + job.getID());
		return job;
	}

	protected GnucashCustomerJobImpl createCustomerJob(final GncGncJob jwsdpJob) {
		GnucashCustomerJobImpl job = new GnucashCustomerJobImpl(jwsdpJob, gcshFile);
		LOGGER.debug("Generated new customer job: " + job.getID());
		return job;
	}

	protected GnucashVendorJobImpl createVendorJob(final GncGncJob jwsdpJob) {
		GnucashVendorJobImpl job = new GnucashVendorJobImpl(jwsdpJob, gcshFile);
		LOGGER.debug("Generated new vendor job: " + job.getID());
		return job;
	}

	// ---------------------------------------------------------------

	public void addGenerJob(GnucashGenerJob job) {
		jobMap.put(job.getID(), job);

		LOGGER.debug("Added (generic) jop to cache: " + job.getID());
	}

	public void removeGenerJob(GnucashGenerJob job) {
		jobMap.remove(job.getID());

		LOGGER.debug("removeGenerJob: No. of generic jobs: " + jobMap.size());
	}

	// ---------------------------------------------------------------

	public GnucashGenerJob getGenerJobByID(final GCshID id) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashGenerJob retval = jobMap.get(id);
		if ( retval == null ) {
			LOGGER.warn("getGenerJobByID: No generic Job with ID '" + id + "'. We know " + jobMap.size() + " jobs.");
		}

		return retval;
	}

	public List<GnucashGenerJob> getGenerJobsByName(String name) {
		return getGenerJobsByName(name, true);
	}

	public List<GnucashGenerJob> getGenerJobsByName(final String expr, final boolean relaxed) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnucashGenerJob> result = new ArrayList<GnucashGenerJob>();

		for ( GnucashGenerJob job : jobMap.values() ) {
			if ( relaxed ) {
				if ( job.getName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
					result.add(job);
				}
			} else {
				if ( job.getName().equals(expr) ) {
					result.add(job);
				}
			}
		}

		return result;
	}

	public GnucashGenerJob getGenerJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		List<GnucashGenerJob> jobList = getGenerJobsByName(name, false);
		if ( jobList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( jobList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return jobList.iterator().next();
	}

	public Collection<GnucashGenerJob> getGenerJobs() {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(jobMap.values());
	}

	// ----------------------------

	public GnucashCustomerJob getCustomerJobByID(final GCshID id) throws WrongJobTypeException {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashGenerJob job = jobMap.get(id);
		if ( job == null ) {
			LOGGER.warn("getCustomerJobByID: No customer Job with ID '" + id + "'. We know " + jobMap.size()
					+ " jobs.");
		}

		return new GnucashCustomerJobImpl(job);
	}

	public List<GnucashCustomerJob> getCustomerJobsByName(String name) throws WrongJobTypeException {
		return getCustomerJobsByName(name, true);
	}

	public List<GnucashCustomerJob> getCustomerJobsByName(final String expr, final boolean relaxed) throws WrongJobTypeException {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnucashCustomerJob> result = new ArrayList<GnucashCustomerJob>();

		for ( GnucashGenerJob job : jobMap.values() ) {
			if ( relaxed ) {
				if ( job.getName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
					result.add(new GnucashCustomerJobImpl(job));
				}
			} else {
				if ( job.getName().equals(expr) ) {
					result.add(new GnucashCustomerJobImpl(job));
				}
			}
		}

		return result;
	}

	public GnucashCustomerJob getCustomerJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException, WrongJobTypeException {
		List<GnucashCustomerJob> jobList = getCustomerJobsByName(name, false);
		if ( jobList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( jobList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return jobList.iterator().next();
	}

	public Collection<GnucashCustomerJob> getCustomerJobs() throws WrongJobTypeException {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		List<GnucashCustomerJob> result = new ArrayList<GnucashCustomerJob>();
		for ( GnucashGenerJob job : jobMap.values() ) {
			if ( job.getOwnerType() == GnucashGenerJob.TYPE_VENDOR ) {
				GnucashCustomerJob custJob = new GnucashCustomerJobImpl(job);
				result.add(custJob);
			}
		}
		
		return result;
	}
	

	// ----------------------------

	public GnucashVendorJob getVendorJobByID(final GCshID id) throws WrongJobTypeException {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashGenerJob job = jobMap.get(id);
		if ( job == null ) {
			LOGGER.warn(
					"getVendorJobByID: No vendor Job with ID '" + id + "'. We know " + jobMap.size() + " jobs.");
		}

		return new GnucashVendorJobImpl(job);
	}

	public List<GnucashVendorJob> getVendorJobsByName(String name) throws WrongJobTypeException {
		return getVendorJobsByName(name, true);
	}

	public List<GnucashVendorJob> getVendorJobsByName(final String expr, final boolean relaxed) throws WrongJobTypeException {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnucashVendorJob> result = new ArrayList<GnucashVendorJob>();

		for ( GnucashGenerJob job : jobMap.values() ) {
			if ( relaxed ) {
				if ( job.getName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
					result.add(new GnucashVendorJobImpl(job));
				}
			} else {
				if ( job.getName().equals(expr) ) {
					result.add(new GnucashVendorJobImpl(job));
				}
			}
		}

		return result;
	}

	public GnucashVendorJob getVendorJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException, WrongJobTypeException {
		List<GnucashVendorJob> jobList = getVendorJobsByName(name, false);
		if ( jobList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( jobList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return jobList.iterator().next();
	}

	/**
	 * @throws WrongJobTypeException 
	 * @see GnucashFile#getGenerJobs()
	 */
	public Collection<GnucashVendorJob> getVendorJobs() throws WrongJobTypeException {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		List<GnucashVendorJob> result = new ArrayList<GnucashVendorJob>();
		for ( GnucashGenerJob job : jobMap.values() ) {
			if ( job.getOwnerType() == GnucashGenerJob.TYPE_VENDOR ) {
				GnucashVendorJob custJob = new GnucashVendorJobImpl(job);
				result.add(custJob);
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	public List<GnucashCustomerJob> getJobsByCustomer(final GnucashCustomer cust) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return FileJobManager_Customer.getJobsByCustomer(this, cust);
	}

	public List<GnucashVendorJob> getJobsByVendor(final GnucashVendor vend) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return FileJobManager_Vendor.getJobsByVendor(this, vend);
	}

	// ---------------------------------------------------------------

	public int getNofEntriesGenerJobMap() {
		return jobMap.size();
	}

}
