package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerJobImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorJobImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FileJobManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager.class);
    
    // ---------------------------------------------------------------
    
    protected GnuCashFileImpl gcshFile;

    protected Map<GCshID, GnuCashGenerJob> jobMap;

    // ---------------------------------------------------------------
    
	public FileJobManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		jobMap = new HashMap<GCshID, GnuCashGenerJob>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncGncJob) ) {
				continue;
			}
			GncGncJob jwsdpJob = (GncGncJob) bookElement;

			try {
				GnuCashGenerJobImpl generJob = createGenerJob(jwsdpJob);
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

	protected GnuCashGenerJobImpl createGenerJob(final GncGncJob jwsdpJob) {
		GnuCashGenerJobImpl job = new GnuCashGenerJobImpl(jwsdpJob, gcshFile);
		LOGGER.debug("Generated new generic job: " + job.getID());
		return job;
	}

	protected GnuCashCustomerJobImpl createCustomerJob(final GncGncJob jwsdpJob) {
		GnuCashCustomerJobImpl job = new GnuCashCustomerJobImpl(jwsdpJob, gcshFile);
		LOGGER.debug("Generated new customer job: " + job.getID());
		return job;
	}

	protected GnuCashVendorJobImpl createVendorJob(final GncGncJob jwsdpJob) {
		GnuCashVendorJobImpl job = new GnuCashVendorJobImpl(jwsdpJob, gcshFile);
		LOGGER.debug("Generated new vendor job: " + job.getID());
		return job;
	}

	// ---------------------------------------------------------------

	public void addGenerJob(GnuCashGenerJob job) {
		jobMap.put(job.getID(), job);

		LOGGER.debug("Added (generic) jop to cache: " + job.getID());
	}

	public void removeGenerJob(GnuCashGenerJob job) {
		jobMap.remove(job.getID());

		LOGGER.debug("removeGenerJob: No. of generic jobs: " + jobMap.size());
	}

	// ---------------------------------------------------------------

	public GnuCashGenerJob getGenerJobByID(final GCshID id) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashGenerJob retval = jobMap.get(id);
		if ( retval == null ) {
			LOGGER.warn("getGenerJobByID: No generic Job with ID '" + id + "'. We know " + jobMap.size() + " jobs.");
		}

		return retval;
	}

	public List<GnuCashGenerJob> getGenerJobsByName(String name) {
		return getGenerJobsByName(name, true);
	}

	public List<GnuCashGenerJob> getGenerJobsByName(final String expr, final boolean relaxed) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashGenerJob> result = new ArrayList<GnuCashGenerJob>();

		for ( GnuCashGenerJob job : jobMap.values() ) {
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

	public GnuCashGenerJob getGenerJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		List<GnuCashGenerJob> jobList = getGenerJobsByName(name, false);
		if ( jobList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( jobList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return jobList.get(0);
	}

	public Collection<GnuCashGenerJob> getGenerJobs() {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(jobMap.values());
	}

	// ----------------------------

	public GnuCashCustomerJob getCustomerJobByID(final GCshID id) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashGenerJob job = jobMap.get(id);
		if ( job == null ) {
			LOGGER.warn("getCustomerJobByID: No customer Job with ID '" + id + "'. We know " + jobMap.size()
					+ " jobs.");
		}

		return new GnuCashCustomerJobImpl(job);
	}

	public List<GnuCashCustomerJob> getCustomerJobsByName(String name) {
		return getCustomerJobsByName(name, true);
	}

	public List<GnuCashCustomerJob> getCustomerJobsByName(final String expr, final boolean relaxed) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashCustomerJob> result = new ArrayList<GnuCashCustomerJob>();

		for ( GnuCashGenerJob job : jobMap.values() ) {
			if ( relaxed ) {
				if ( job.getName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
					result.add(new GnuCashCustomerJobImpl(job));
				}
			} else {
				if ( job.getName().equals(expr) ) {
					result.add(new GnuCashCustomerJobImpl(job));
				}
			}
		}

		return result;
	}

	public GnuCashCustomerJob getCustomerJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		List<GnuCashCustomerJob> jobList = getCustomerJobsByName(name, false);
		if ( jobList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( jobList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return jobList.get(0);
	}

	public Collection<GnuCashCustomerJob> getCustomerJobs() {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		List<GnuCashCustomerJob> result = new ArrayList<GnuCashCustomerJob>();
		for ( GnuCashGenerJob job : jobMap.values() ) {
			if ( job.getOwnerType() == GnuCashGenerJob.TYPE_VENDOR ) {
				GnuCashCustomerJob custJob = new GnuCashCustomerJobImpl(job);
				result.add(custJob);
			}
		}
		
		return result;
	}
	

	// ----------------------------

	public GnuCashVendorJob getVendorJobByID(final GCshID id) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashGenerJob job = jobMap.get(id);
		if ( job == null ) {
			LOGGER.warn(
					"getVendorJobByID: No vendor Job with ID '" + id + "'. We know " + jobMap.size() + " jobs.");
		}

		return new GnuCashVendorJobImpl(job);
	}

	public List<GnuCashVendorJob> getVendorJobsByName(String name) {
		return getVendorJobsByName(name, true);
	}

	public List<GnuCashVendorJob> getVendorJobsByName(final String expr, final boolean relaxed) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashVendorJob> result = new ArrayList<GnuCashVendorJob>();

		for ( GnuCashGenerJob job : jobMap.values() ) {
			if ( relaxed ) {
				if ( job.getName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
					result.add(new GnuCashVendorJobImpl(job));
				}
			} else {
				if ( job.getName().equals(expr) ) {
					result.add(new GnuCashVendorJobImpl(job));
				}
			}
		}

		return result;
	}

	public GnuCashVendorJob getVendorJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		List<GnuCashVendorJob> jobList = getVendorJobsByName(name, false);
		if ( jobList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( jobList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return jobList.get(0);
	}

	/**
	 * @see GnuCashFile#getGenerJobs()
	 */
	public Collection<GnuCashVendorJob> getVendorJobs() {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		List<GnuCashVendorJob> result = new ArrayList<GnuCashVendorJob>();
		for ( GnuCashGenerJob job : jobMap.values() ) {
			if ( job.getOwnerType() == GnuCashGenerJob.TYPE_VENDOR ) {
				GnuCashVendorJob custJob = new GnuCashVendorJobImpl(job);
				result.add(custJob);
			}
		}
		
		return result;
	}

	// ---------------------------------------------------------------

	public List<GnuCashCustomerJob> getJobsByCustomer(final GnuCashCustomer cust) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return FileJobManager_Customer.getJobsByCustomer(this, cust);
	}

	public List<GnuCashVendorJob> getJobsByVendor(final GnuCashVendor vend) {
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
